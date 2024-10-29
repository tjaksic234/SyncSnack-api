package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.services.RateLimitService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@AllArgsConstructor
public class RateLimitServiceImpl implements RateLimitService {

    //private final RedissonClient redissonClient;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

   /* @Override
    public boolean tryAcquire(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.PER_CLIENT, 5, 5, RateIntervalUnit.MINUTES);
        boolean acquired = rateLimiter.tryAcquire();
        log.warn("Rate limit check for user {}: {}. Available permits: {}",
                key, acquired ? "allowed" : "blocked", rateLimiter.availablePermits());
        return acquired;
    }*/

    //* this code is for rate limiting at the application level
    //* in case something goes wrong with the redis server implementation
    //? this serves as a backup
    @Override
    public boolean allowRequest(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, this::newBucket);
        return bucket.tryConsume(1);
    }

    @Override
    public Bucket newBucket(String key) {
        Bandwidth bandwidth = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofMinutes(5))
                .build();
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}
