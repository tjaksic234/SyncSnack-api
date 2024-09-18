package com.example.KavaSpring.services;

import io.github.bucket4j.Bucket;

public interface RateLimitService {
   // boolean tryAcquire(String key);
    boolean allowRequest(String key);
    Bucket newBucket(String key);
}
