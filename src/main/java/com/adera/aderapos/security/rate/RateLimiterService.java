package com.adera.aderapos.security.rate;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service for rate limiting user requests.
 */
@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, UserRequestInfo> userRequestMap = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 50; // e.g., max 50 requests
    private final long WINDOW_MS = 60_000; // 1 minute

    /**
     * Checks if a request is allowed for the given key.
     *
     * @param key the identifier for the user/requester
     * @return true if the request is allowed, false otherwise
     */
    public boolean allowRequest(String key) {
        UserRequestInfo info = userRequestMap.computeIfAbsent(key, k -> new UserRequestInfo());
        synchronized (info) {
            long now = Instant.now().toEpochMilli();
            if (now - info.startTime > WINDOW_MS) {
                info.startTime = now;
                info.count.set(0);
            }
            if (info.count.incrementAndGet() <= MAX_REQUESTS) {
                return true;
            }
            return false;
        }
    }

    /**
     * Resets the request count for the given key.
     */
    private static class UserRequestInfo {
        long startTime = Instant.now().toEpochMilli();
        AtomicInteger count = new AtomicInteger(0);
    }
}
