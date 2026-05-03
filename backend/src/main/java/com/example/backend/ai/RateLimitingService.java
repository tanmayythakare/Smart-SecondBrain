package com.example.backend.ai;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimitingService {
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private final ConcurrentHashMap<Long, RequestTracker> userRequests = new ConcurrentHashMap<>();

    public boolean isAllowed(Long userId) {
        RequestTracker tracker = userRequests.computeIfAbsent(userId, k -> new RequestTracker());
        long now = System.currentTimeMillis();
        
        // Reset count every minute
        if (now - tracker.lastResetTime.get() > 60000) {
            tracker.count.set(0);
            tracker.lastResetTime.set(now);
        }
        
        return tracker.count.incrementAndGet() <= MAX_REQUESTS_PER_MINUTE;
    }

    private static class RequestTracker {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
    }
}
