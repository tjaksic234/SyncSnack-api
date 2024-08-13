package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.models.dto.MobileNotification;
import com.example.KavaSpring.services.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public String sendNotification(MobileNotification mobileNotification, String token) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(mobileNotification.getSubject())
                .setBody(mobileNotification.getContent())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(mobileNotification.getData())
                .build();

        return firebaseMessaging.send(message);
    }
}
