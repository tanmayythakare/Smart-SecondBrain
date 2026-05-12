package com.example.backend.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmbeddingService {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingService.class);
    @Value("${spring.ai.google.gemini.api-key}")
    private String apiKey;
    private final String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/text-embedding-004:embedContent";
    private final RestTemplate restTemplate;
    private final Map<String, List<Double>> embeddingCache = new ConcurrentHashMap<>();

    public EmbeddingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Double> getEmbedding(String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        if (embeddingCache.containsKey(text)) {
            return embeddingCache.get(text);
        }
        String url = baseUrl + "?key=" + apiKey;
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Collections.singletonList(Collections.singletonMap("text", text)));
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "models/text-embedding-004");
        requestBody.put("content", content);
        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            if (response != null && response.containsKey("embedding")) {
                Map<String, Object> embeddingObj = (Map<String, Object>) response.get("embedding");
                List<Double> values = (List<Double>) embeddingObj.get("values");
                embeddingCache.put(text, values);
                return values;
            }
        } catch (Exception e) {
            log.error("Failed to fetch embedding from Gemini", e);
        }
        return Collections.emptyList();
    }

    public double calculateSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.isEmpty() || v2.isEmpty() || v1.size() != v2.size())
            return 0;
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}