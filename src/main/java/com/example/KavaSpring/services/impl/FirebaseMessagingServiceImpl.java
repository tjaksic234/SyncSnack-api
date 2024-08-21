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

import java.util.*;


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
                .build();

        Message.Builder messageBuilder = Message
                .builder()
                .setToken(token)
                .setNotification(notification)
                .putAllData(mobileNotification.getData());

        if (mobileNotification.getImage() != null && !mobileNotification.getImage().isEmpty()) {
            messageBuilder.setAndroidConfig(setupAndroidConfig(mobileNotification.getImage()));
        }
        return firebaseMessaging.send(messageBuilder.build());
    }

    @Override
    public String sendMulticastNotification(MobileNotification mobileNotification, List<String> tokens) throws FirebaseMessagingException {
        Notification notification = Notification
                .builder()
                .setTitle(mobileNotification.getTitle())
                .setBody(mobileNotification.getContent())
                .build();

        MulticastMessage.Builder messageBuilder = MulticastMessage
                .builder()
                .addAllTokens(tokens)
                .setNotification(notification)
                .putAllData(mobileNotification.getData());

        return firebaseMessaging.sendEachForMulticast(messageBuilder.build()).toString();
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

        //? Setting the notification display order
        String title = "New order placed";

        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append(orderNotification.getFirstName())
                .append(" ")
                .append(orderNotification.getLastName())
                .append(" wants to order: ");

        orderNotification.getAdditionalOptions().forEach((key, value) ->
                contentBuilder.append(key).append(": ").append(value).append(", ")
        );
        contentBuilder.setLength(contentBuilder.length() - 2);
        String content = contentBuilder.toString();

        Map<String, String> data = new HashMap<>();
        data.put("orderId", orderNotification.getOrderId());
        data.put("eventId", order.getEventId());

        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setTitle(title);
        mobileNotification.setContent(content);
        mobileNotification.setData(data);
        mobileNotification.setImage(orderNotification.getProfilePhoto());

        //? sending the notification to the event creator on mobile
        sendNotification(mobileNotification, fcmToken);
    }

    @Override
    public void notifyGroupOfNewEvent(Event event) throws FirebaseMessagingException {
        List<UserProfile> userProfilesWithTokens = userProfileRepository.findByGroupIdAndFcmTokenIsNotNull(event.getGroupId());
        List<String> tokens = new ArrayList<>();

        for (UserProfile userProfile : userProfilesWithTokens) {
            tokens.add(userProfile.getFcmToken());
        }

        String title = "New event created for your group";
        String content = "New Event: " + event.getTitle() + "\n\n" +
                "Type: " + event.getEventType() + "\n" +
                "Description: " +
                event.getDescription() +
                "\n" +
                "Created: " + event.getCreatedAt() + "\n" +
                "Pending until: " + event.getPendingUntil();

        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setTitle(title);
        mobileNotification.setContent(content);
        sendMulticastNotification(mobileNotification, tokens);
    }

    @Override
    public AndroidConfig setupAndroidConfig(String imageUrl) {
        return AndroidConfig.builder()
                .setNotification(AndroidNotification.builder()
                        .setImage(imageUrl)
                        .setPriority(AndroidNotification.Priority.HIGH)
                        .setColor("#FF0000")
                        .build())
                .build();
    }


}
