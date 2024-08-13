package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.MobileNotification;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface FirebaseMessagingService {
    String sendNotification(MobileNotification mobileNotification, String token) throws FirebaseMessagingException;
}
