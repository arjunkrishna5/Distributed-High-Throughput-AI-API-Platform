// REST Controller exposing public HTTP API endpoints for AI prediction requests.
// Connects browser requests from Swagger UI to Python AI Engine over gRPC.

package com.platform.ai.gateway.controller;

import com.platform.ai.gateway.service.GrpcClientService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PredictController {

    private final GrpcClientService grpcClientService;

    public PredictController(GrpcClientService grpcClientService) {
        this.grpcClientService = grpcClientService;
    }

    @PostMapping("/predict")
    public Map<String, Object> predict(@RequestBody Map<String, String> request) {
        String inputText = request.getOrDefault("text", "I love AI engineering!");
        return this.grpcClientService.getPrediction(inputText);
    }
}
