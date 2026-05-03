package com.example.backend.security;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiter {

    private final Map<String, RequestCounter> counters = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 10;
    private final long WINDOW_MS = TimeUnit.MINUTES.toMillis(1);

    public boolean isAllowed(String key) {
        if (key == null) return true; // Fallback if IP cannot be determined
        long now = System.currentTimeMillis();
        RequestCounter counter = counters.compute(key, (k, v) -> {
            if (v == null || now - v.startTime > WINDOW_MS) {
                return new RequestCounter(now, new AtomicInteger(1));
            }
            v.count.incrementAndGet();
            return v;
        });
        return counter.count.get() <= MAX_REQUESTS;
    }

    private static class RequestCounter {
        final long startTime;
        final AtomicInteger count;

        RequestCounter(long startTime, AtomicInteger count) {
            this.startTime = startTime;
            this.count = count;
        }
    }
}
