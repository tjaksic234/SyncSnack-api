package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.NotificationRequest;

import java.util.concurrent.ExecutionException;

public interface FCMService {
    void sendNotificationToTopic(NotificationRequest request) throws ExecutionException, InterruptedException;
}
