package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.MobileNotification;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

public interface FirebaseMessagingService {
    String sendNotification(MobileNotification mobileNotification, String token) throws FirebaseMessagingException;
    BatchResponse sendMulticastNotification(MobileNotification mobileNotification, List<String> tokens) throws FirebaseMessagingException;
    void notifyEventCreatorOfNewOrder(Order order) throws FirebaseMessagingException;
    void notifyGroupOfNewEvent(Event event) throws FirebaseMessagingException;
    AndroidConfig setupAndroidConfig(String imageUrl);
}
