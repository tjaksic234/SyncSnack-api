package com.example.KavaSpring.services;

import io.github.bucket4j.Bucket;

public interface RateLimiterService {
    boolean allowRequest(String key);
    Bucket newBucket(String key);
}
