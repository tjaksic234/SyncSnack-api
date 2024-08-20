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
import com.google.firebase.messaging.*;
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
                .setTitle(mobileNotification.getTitle())
                .setBody(mobileNotification.getContent())
                .setImage(mobileNotification.getImage())
                .build();

        Message.Builder messageBuilder = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(mobileNotification.getData());

       /* if (mobileNotification.getImage() != null && !mobileNotification.getImage().isEmpty()) {
            messageBuilder.setAndroidConfig(setupAndroidConfig(mobileNotification.getImage()));
        }*/
        return firebaseMessaging.send(messageBuilder.build());
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
            log.warn("FCM token not found for event creator user profile: {}. Skipping mobile notification.", userProfile.getId());
            return;
        }

        //? Convert the order into an order notification
        OrderNotification orderNotification = converterService.convertOrderToOrderNotification(order);

        String subject = "New order placed";
        String content = userProfile.getFirstName() + " " + userProfile.getLastName() + " wants to order";

        Map<String, String> data = new HashMap<>();
        data.put("image", orderNotification.getProfilePhoto());

        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setTitle(subject);
        mobileNotification.setContent(content);
        mobileNotification.setData(data);
        //mobileNotification.setImage(orderNotification.getProfilePhoto());
        log.warn("The image: {}", mobileNotification.getImage());

        //? sending the notification to the event creator on mobile
        sendNotification(mobileNotification, fcmToken);
    }

    @Override
    public AndroidConfig setupAndroidConfig(String imageUrl) {
        return AndroidConfig.builder()
                .setNotification(AndroidNotification.builder()
                        .setImage(imageUrl)
                        .setColor("#FF0000")
                        .build())
                .build();
    }


}
