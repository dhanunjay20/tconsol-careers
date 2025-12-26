package com.tcon.careers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RateLimitService {

    @Value("${app.rate-limit.applications-per-hour}")
    private int applicationsPerHour;

    private final Map<String, ApplicationRateTracker> rateLimitMap = new ConcurrentHashMap<>();

    public void checkRateLimit(String email) {
        ApplicationRateTracker tracker = rateLimitMap.computeIfAbsent(email, k -> new ApplicationRateTracker());

        tracker.cleanup();

        if (tracker.getCount() >= applicationsPerHour) {
            log.warn("Rate limit exceeded for email: {}", email);
            throw new RuntimeException("Too many applications. Please try again later.");
        }

        tracker.increment();
    }

    private static class ApplicationRateTracker {
        private int count = 0;
        private LocalDateTime windowStart = LocalDateTime.now();

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }

        public void cleanup() {
            LocalDateTime now = LocalDateTime.now();
            if (windowStart.plusHours(1).isBefore(now)) {
                count = 0;
                windowStart = now;
            }
        }
    }
}

