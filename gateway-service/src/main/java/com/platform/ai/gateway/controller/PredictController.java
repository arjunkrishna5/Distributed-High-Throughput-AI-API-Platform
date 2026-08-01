// REST Controller exposing public HTTP API endpoints for AI prediction requests.
// Enforces Token Bucket rate limiting before calling Python AI Engine over gRPC.

package com.platform.ai.gateway.controller;

import com.platform.ai.gateway.service.GrpcClientService;
import com.platform.ai.gateway.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PredictController {

    private final GrpcClientService grpcClientService;
    private final RateLimitingService rateLimitingService;

    public PredictController(GrpcClientService grpcClientService, RateLimitingService rateLimitingService) {
        this.grpcClientService = grpcClientService;
        this.rateLimitingService = rateLimitingService;
    }

    @PostMapping("/predict")
    public ResponseEntity<Map<String, Object>> predict(@RequestBody Map<String, String> request) {
        Bucket bucket = rateLimitingService.resolveBucket("user-client-ip");

        if (bucket.tryConsume(1)) {
            String inputText = request.getOrDefault("text", "I love AI engineering!");
            Map<String, Object> prediction = this.grpcClientService.getPrediction(inputText);
            return ResponseEntity.ok(prediction);
        } else {
            Map<String, Object> errorResponse = Map.of(
                "error", "Too Many Requests",
                "message", "Rate limit exceeded. Maximum 10 requests allowed per minute.",
                "status", 429
            );
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }
    }
}
