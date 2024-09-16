package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.services.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@AllArgsConstructor
public class RateLimiterServiceImpl implements RateLimiterService {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean allowRequest(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, this::newBucket);
        return bucket.tryConsume(1);
    }

    @Override
    public Bucket newBucket(String key) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofMinutes(5))
                .build();
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}
