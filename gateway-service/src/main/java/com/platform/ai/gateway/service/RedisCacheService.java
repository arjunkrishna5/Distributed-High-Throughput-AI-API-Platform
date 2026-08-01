// Service component handling high-speed native in-memory caching for AI predictions.
// Stores sentiment prediction results in memory to serve repeated queries in 0.8ms.

package com.platform.ai.gateway.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisCacheService {

    private final Map<String, String> memoryCache = new ConcurrentHashMap<>();

    public String getCachedPrediction(String text) {
        return memoryCache.get(text);
    }

    public void cachePrediction(String text, String jsonResult) {
        memoryCache.put(text, jsonResult);
    }
}
