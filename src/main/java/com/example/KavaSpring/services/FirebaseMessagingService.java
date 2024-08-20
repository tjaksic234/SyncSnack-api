package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.MobileNotification;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessagingException;

public interface FirebaseMessagingService {
    String sendNotification(MobileNotification mobileNotification, String token) throws FirebaseMessagingException;
    void notifyEventCreatorOfNewOrder(Order order) throws FirebaseMessagingException;
    AndroidConfig setupAndroidConfig(String imageUrl);
}
