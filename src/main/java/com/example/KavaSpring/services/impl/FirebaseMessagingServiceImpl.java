package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.MobileNotification;
import com.example.KavaSpring.models.dto.OrderNotification;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.services.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    private final UserProfileRepository userProfileRepository;

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    @Override
    public String sendNotification(MobileNotification mobileNotification, String token) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(mobileNotification.getSubject())
                .setBody(mobileNotification.getContent())
                .setImage(mobileNotification.getImage())
                .build();

        Message message = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(mobileNotification.getData())
                .build();


        return firebaseMessaging.send(message);
    }

    @Override
    public void notifyEventCreatorOfNewOrder(Order order) throws FirebaseMessagingException {
        Optional<Event> event = eventRepository.findById(order.getEventId());
        if (event.isEmpty()) {
            throw new IllegalStateException("No event present for the order being placed");
        }

        String recipientUserProfileId = event.get().getUserProfileId();

        UserProfile userProfile = userProfileRepository.findById(recipientUserProfileId)
                .orElseThrow(() -> new NotFoundException("Event creator user profile not found"));

        String fcmToken = userProfile.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token not found for event creator user profile: {}", userProfile.getId());
            throw new NotFoundException("FCM token not found for event user profile");
        }

        //? Convert the order into an order notification
        OrderNotification orderNotification = converterService.convertOrderToOrderNotification(order);

        String subject = "New order placed";
        String content = userProfile.getFirstName() + " " + userProfile.getLastName() + "wants to order";

        Map<String, String> data = new HashMap<>();
        data.put("test", "test");

        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setSubject(subject);
        mobileNotification.setContent(content);
        mobileNotification.setData(data);
        mobileNotification.setImage(orderNotification.getProfilePhoto());

        //? sending the notification to the event creator on mobile
        sendNotification(mobileNotification, fcmToken);
    }
}
