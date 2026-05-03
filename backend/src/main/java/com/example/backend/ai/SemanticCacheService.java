package com.example.backend.ai;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;

@Service
@Slf4j
public class SemanticCacheService {

    private final EmbeddingService embeddingService;
    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;

    public SemanticCacheService(EmbeddingService embeddingService, MeterRegistry meterRegistry) {
        this.embeddingService = embeddingService;
        this.cacheHitCounter = Counter.builder("ai.cache.semantic.hit")
                .description("Number of semantic cache hits")
                .register(meterRegistry);
        this.cacheMissCounter = Counter.builder("ai.cache.semantic.miss")
                .description("Number of semantic cache misses")
                .register(meterRegistry);
    }
    
    // In-memory cache: User ID -> Thread-safe List of cached entries
    private final Map<Long, List<CacheEntry>> cache = new ConcurrentHashMap<>();
    
    private static final double SIMILARITY_THRESHOLD = 0.96;
    private static final long CACHE_TTL_MS = 3_600_000; // 1 hour

    @Data
    @Builder
    public static class CacheEntry {
        private String query;
        private List<Double> embedding;
        private String response;
        private long timestamp;
    }

    public void put(Long userId, String query, List<Double> embedding, String response) {
        List<CacheEntry> userCache = cache.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());
        
        userCache.add(CacheEntry.builder()
                .query(query)
                .embedding(embedding)
                .response(response)
                .timestamp(System.currentTimeMillis())
                .build());
        
        // Keep only last 50 entries per user to bound memory usage
        if (userCache.size() > 50) {
            userCache.remove(0);
        }
    }

    public String get(Long userId, String query, List<Double> queryEmbedding) {
        List<CacheEntry> userCache = cache.get(userId);
        if (userCache == null || queryEmbedding == null || queryEmbedding.isEmpty()) return null;

        String normalizedQuery = query.trim().toLowerCase();
        long now = System.currentTimeMillis();
        
        for (CacheEntry entry : userCache) {
            // BUG FIX: Add TTL Expiry check (Phase 10.2)
            if (now - entry.getTimestamp() > CACHE_TTL_MS) {
                userCache.remove(entry);
                continue;
            }

            double similarity = embeddingService.calculateSimilarity(queryEmbedding, entry.getEmbedding());
            if (similarity >= SIMILARITY_THRESHOLD) {
                log.info("Semantic cache hit for user {}: '{}' (Similarity: {})", userId, normalizedQuery, String.format("%.4f", similarity));
                cacheHitCounter.increment();
                return entry.getResponse();
            }
        }
        cacheMissCounter.increment();
        return null;
    }
}
