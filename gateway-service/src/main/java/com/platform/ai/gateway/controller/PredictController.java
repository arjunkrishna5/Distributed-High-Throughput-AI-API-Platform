// REST Controller exposing public HTTP API endpoints for AI prediction requests.
// Handles incoming browser requests and exposes Swagger UI interactive documentation.

package com.platform.ai.gateway.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PredictController {

    @PostMapping("/predict")
    public Map<String, Object> predict(@RequestBody Map<String, String> request) {
        String inputText = request.getOrDefault("text", "");
        return Map.of(
            "message", "Received by Java Gateway!",
            "input_text", inputText,
            "status", "SUCCESS"
        );
    }
}
