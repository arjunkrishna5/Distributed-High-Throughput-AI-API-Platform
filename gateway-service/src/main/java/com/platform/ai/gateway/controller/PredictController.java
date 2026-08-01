// REST Controller exposing public HTTP API endpoints for AI prediction requests.
// Integrates Redis AI prediction caching, Token Bucket rate limiting, and gRPC routing.

package com.platform.ai.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.gateway.service.GrpcClientService;
import com.platform.ai.gateway.service.RateLimitingService;
import com.platform.ai.gateway.service.RedisCacheService;
import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PredictController {

    private final GrpcClientService grpcClientService;
    private final RateLimitingService rateLimitingService;
    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper;

    public PredictController(GrpcClientService grpcClientService,
                             RateLimitingService rateLimitingService,
                             RedisCacheService redisCacheService,
                             ObjectMapper objectMapper) {
        this.grpcClientService = grpcClientService;
        this.rateLimitingService = rateLimitingService;
        this.redisCacheService = redisCacheService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, String> request) {
        Bucket bucket = rateLimitingService.resolveBucket("user-client-ip");

        if (bucket.tryConsume(1)) {
            String inputText = request.getOrDefault("text", "I love AI engineering!");

            // 1. Check Redis Cache
            String cachedJson = redisCacheService.getCachedPrediction(inputText);
            if (cachedJson != null) {
                try {
                    Map<String, Object> cachedResult = objectMapper.readValue(cachedJson, new TypeReference<Map<String, Object>>() {});
                    Map<String, Object> response = new HashMap<>(cachedResult);
                    response.put("cache_hit", true);
                    return ResponseEntity.ok(response);
                } catch (Exception e) {
                    // Fallback to gRPC on JSON parse error
                }
            }

            // 2. Cache Miss: Execute gRPC PyTorch Inference
            Map<String, Object> prediction = this.grpcClientService.getPrediction(inputText);
            Map<String, Object> response = new HashMap<>(prediction);
            response.put("cache_hit", false);

            // 3. Store result in Redis Cache
            try {
                String jsonToCache = objectMapper.writeValueAsString(prediction);
                redisCacheService.cachePrediction(inputText, jsonToCache);
            } catch (Exception e) {
                // Continue safely if cache store fails
            }

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> errorResponse = Map.of(
                "error", "Too Many Requests",
                "message", "Rate limit exceeded. Maximum 100 requests allowed per minute.",
                "status", 429
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
    }
}
