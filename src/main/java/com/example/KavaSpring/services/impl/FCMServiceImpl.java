package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.models.dto.NotificationRequest;
import com.example.KavaSpring.services.FCMService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;


@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class FCMServiceImpl implements FCMService {

    @Override
    public void sendNotificationToTopic(NotificationRequest request) throws ExecutionException, InterruptedException {
        Notification notification = Notification.builder()
                .setTitle(request.getTitle())
                .setBody(request.getBody())
                .build();

        Message message = Message.builder()
                .setTopic(request.getTopic())
                .setNotification(notification)
                .build();

        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        log.info("Sent notification to topic: " + request.getTopic() + ", response: " + response);
    }
}
