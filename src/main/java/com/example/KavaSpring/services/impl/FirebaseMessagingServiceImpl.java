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
    public BatchResponse sendMulticastNotification(MobileNotification mobileNotification, List<String> tokens) throws FirebaseMessagingException {
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

        BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(messageBuilder.build());
        //? logging the batch results
        logBatchResults(batchResponse, tokens);
        return batchResponse;
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
        data.put("eventId", orderNotification.getEventId());
        data.put("screen", "all-orders");

        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setTitle(title);
        mobileNotification.setContent(content);
        mobileNotification.setData(data);
        mobileNotification.setImage(orderNotification.getProfilePhoto());

        //? sending the notification to the event creator on mobile
        log.info("Sending mobile notification to the event creator of the new order");
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
                "Pending until: " + event.getPendingUntil();

        Map<String, String> data = new HashMap<>();
        data.put("eventId", event.getId());
        data.put("screen", "event_details");


        MobileNotification mobileNotification = new MobileNotification();
        mobileNotification.setTitle(title);
        mobileNotification.setContent(content);
        mobileNotification.setData(data);

        log.info("Sending mobile notification to the entire group");
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

    private void logBatchResults(BatchResponse batchResponse, List<String> tokens) {
        int successCount = 0;
        for (int i = 0; i < batchResponse.getResponses().size(); i++) {
            if (batchResponse.getResponses().get(i).isSuccessful()) {
                successCount++;
            } else {
                log.warn("Failed to send notification to token: {}. Error: {}",
                        tokens.get(i), batchResponse.getResponses().get(i).getException().getMessage());
            }
        }
        log.info("Batch result: {} successful, {} failed", successCount, tokens.size() - successCount);
    }

}
