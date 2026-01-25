package com.mlbstats.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties rateLimitProperties;
    private final Map<String, RateLimitBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientId = getClientIdentifier(request);
        RateLimitBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new RateLimitBucket(rateLimitProperties.getRequestsPerMinute()));

        if (bucket.tryConsume()) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimitProperties.getRequestsPerMinute()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getRemaining()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Try again later.\"}");
        }

        cleanupOldBuckets();
    }

    private String getClientIdentifier(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupOldBuckets() {
        long now = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> now - entry.getValue().getLastAccess() > 120_000);
    }

    private static class RateLimitBucket {
        private final int maxRequests;
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();
        private volatile long lastAccess = System.currentTimeMillis();

        RateLimitBucket(int maxRequests) {
            this.maxRequests = maxRequests;
        }

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            lastAccess = now;

            if (now - windowStart > 60_000) {
                windowStart = now;
                count.set(0);
            }

            if (count.get() < maxRequests) {
                count.incrementAndGet();
                return true;
            }
            return false;
        }

        int getRemaining() {
            return Math.max(0, maxRequests - count.get());
        }

        long getLastAccess() {
            return lastAccess;
        }
    }
}
