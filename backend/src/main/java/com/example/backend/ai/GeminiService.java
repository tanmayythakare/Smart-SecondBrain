package com.example.backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.annotation.PostConstruct;


import java.util.*;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    @Value("${spring.ai.google.gemini.api-key}")
    private String apiKey;

    @Value("${spring.ai.google.gemini.model}")
    private String geminiModel;

    private final String geminiEndpoint = "https://generativelanguage.googleapis.com/v1beta/models/";

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public GeminiService(RestTemplate restTemplate, WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    @PostConstruct
    public void debugGeminiConfig() {
        String masked = apiKey == null
                ? "NULL"
                : (apiKey.length() > 10 ? apiKey.substring(0, 10) + "..." : "TOO_SHORT");

        System.out.println("=== GEMINI DEBUG ===");
        System.out.println("Model: " + geminiModel);
        System.out.println("API Key: " + masked);
        System.out.println("====================");
    }

    public String generate(String prompt) {
        return callGemini(prompt);
    }

    public Flux<String> streamGenerate(String prompt) {
        String url = geminiEndpoint + geminiModel + ":streamGenerateContent?key=" + apiKey + "&alt=sse";
        log.info("Starting Gemini Stream call ({})...", geminiModel);

        Map<String, Object> part = Collections.singletonMap("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(content));

        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                    response -> response.bodyToMono(String.class)
                        .flatMap(body -> {
                            log.error("Gemini API Error Response: {}", body);
                            return Mono.error(new RuntimeException("Gemini API error: " + body));
                        })
                )
                .bodyToFlux(String.class)
                .filter(json -> !json.isEmpty() && !json.trim().equals("[DONE]"))
                .map(this::extractTextFromJson)
                .filter(text -> !text.isEmpty())
                .doOnError(e -> log.error("Gemini Stream Exception: {}", e.getMessage()));
    }

    private String extractTextFromJson(String json) {
        try {
            Map<String, Object> parsed = objectMapper.readValue(json, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) parsed.get("candidates");
            if (candidates == null || candidates.isEmpty()) return "";

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) return "";

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) return "";

            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "";
        }
    }

    private String callGemini(String prompt) {
        String url = geminiEndpoint + geminiModel + ":generateContent?key=" + apiKey;
        Map<String, Object> part = Collections.singletonMap("text", prompt);
        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");
        content.put("parts", Collections.singletonList(part));
        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(content));

        try {
            log.info("Calling Gemini API (Sync, {})...", geminiModel);
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> contentObj = (Map<String, Object>) firstCandidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentObj.get("parts");
                    if (!parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            log.error("Gemini API Error Response: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Gemini API error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Gemini sync call error ({}): {}", geminiModel, e.getMessage());
            throw new RuntimeException("AI Reasoning Layer unreachable. Check model configuration or API key.");
        }

        return "{\"message\": \"AI returned an empty response.\", \"items\": []}";
    }
}