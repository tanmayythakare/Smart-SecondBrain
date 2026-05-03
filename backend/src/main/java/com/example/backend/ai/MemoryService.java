package com.example.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryService {

    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    
    // In-memory summary storage: User ID -> Latest Summary
    private final Map<Long, String> summaryCache = new ConcurrentHashMap<>();

    /**
     * Non-blocking retrieval of history context. 
     * Uses the last known summary + recent messages.
     */
    public String getConversationContext(Long userId, List<String> history) {
        if (history == null || history.isEmpty()) return "";
        
        if (history.size() >= 10) {
            // Trigger background summarization if we hit the threshold
            triggerAsyncSummarization(userId, history);
        }

        String cachedSummary = summaryCache.getOrDefault(userId, "");
        int windowStart = Math.max(0, history.size() - 4); // Keep last 4 messages raw
        String recentMessages = String.join("\n", history.subList(windowStart, history.size()));

        return cachedSummary.isEmpty() ? recentMessages : "[PREVIOUS SUMMARY]\n" + cachedSummary + "\n\n[RECENT MESSAGES]\n" + recentMessages;
    }

    @Async
    public void triggerAsyncSummarization(Long userId, List<String> history) {
        log.info("Async summarization triggered for user {}", userId);
        
        String historyText = String.join("\n", history);
        String prompt = "Summarize the following conversation history between a User and an AI Assistant in 2-3 concise bullet points. " +
                "Focus on the key data discussed and user preferences.\n\n" +
                "History:\n" + historyText + "\n\nRespond in JSON: {\"message\": \"summary text here\"}";

        try {
            String rawResponse = geminiService.generate(prompt);
            JsonNode node = objectMapper.readTree(rawResponse);
            String summary = node.has("message") ? node.get("message").asText() : rawResponse;
            
            summaryCache.put(userId, summary.trim());
            log.info("Successfully updated summary for user {}", userId);
        } catch (Exception e) {
            log.warn("Async summarization failed for user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Legacy method kept for compatibility, now optimized with robust parsing.
     */
    public String summarize(List<String> history) {
        if (history == null || history.isEmpty()) return "";
        return String.join("\n", history);
    }

    public String getLatestInsight(Long userId) {
        return summaryCache.getOrDefault(userId, "");
    }
}
