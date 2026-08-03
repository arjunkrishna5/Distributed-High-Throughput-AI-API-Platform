// REST Controller exposing public HTTP API endpoints for AI prediction requests.
// Integrates native in-memory prediction caching, per-IP rate limiting, and gRPC routing.

package com.platform.ai.gateway.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.ai.gateway.service.GrpcClientService;
import com.platform.ai.gateway.service.InMemoryCacheService;
import com.platform.ai.gateway.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
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
    private final InMemoryCacheService cacheService;
    private final ObjectMapper objectMapper;

    public PredictController(GrpcClientService grpcClientService,
                             RateLimitingService rateLimitingService,
                             InMemoryCacheService cacheService,
                             ObjectMapper objectMapper) {
        this.grpcClientService = grpcClientService;
        this.rateLimitingService = rateLimitingService;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, String> requestPayload,
                                                       HttpServletRequest servletRequest) {
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = "default-client";
        }

        Bucket bucket = rateLimitingService.resolveBucket(clientIp);

        if (bucket.tryConsume(1)) {
            String inputText = requestPayload.getOrDefault("text", "I love AI engineering!");

            // 1. Check In-Memory Cache
            String cachedJson = cacheService.getCachedPrediction(inputText);
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

            // 3. Store result in In-Memory Cache
            try {
                String jsonToCache = objectMapper.writeValueAsString(prediction);
                cacheService.cachePrediction(inputText, jsonToCache);
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
