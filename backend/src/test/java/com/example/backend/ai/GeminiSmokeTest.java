package com.example.backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * Direct Smoke Test for GeminiService logic without full Spring context.
 */
public class GeminiSmokeTest {
    public static void main(String[] args) {
        String apiKey = System.getenv("SPRING_AI_GOOGLE_GEMINI_API_KEY");
        String baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash";
        String url = baseUrl + ":generateContent?key=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        
        Map<String, Object> part = Collections.singletonMap("text", "Hello, are you operational?");
        Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(part));
        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(content));

        System.out.println("Testing URL: " + url.replace(apiKey, "REDACTED"));
        
        try {
            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);
            System.out.println("SUCCESS! Response received.");
            System.out.println(response);
        } catch (Exception e) {
            System.err.println("FAILURE! Error details:");
            e.printStackTrace();
        }
    }
}
