package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.MobileNotification;
import com.example.KavaSpring.models.dto.OrderNotification;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.GroupMembershipRepository;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.FirebaseMessageTemplates;
import com.example.KavaSpring.services.FirebaseMessagingService;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class FirebaseMessagingServiceImpl implements FirebaseMessagingService {

    private final FirebaseMessaging firebaseMessaging;

    private final UserProfileRepository userProfileRepository;

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    private final GroupMembershipRepository groupMembershipRepository;

    private final GroupRepository groupRepository;

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

        Group group = groupRepository.findById(order.getGroupId())
                .orElseThrow(() -> new NoGroupFoundException("No group associated with the groupId"));

        //? Convert the order into an order notification
        OrderNotification orderNotification = converterService.convertOrderToOrderNotification(order);

        //? Setting the notification display order
        String title = FirebaseMessageTemplates.NEW_ORDER_TITLE;
        String content = FirebaseMessageTemplates.buildNewOrderContent(orderNotification, group.getName());

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
        List<GroupMembership> groupMembershipList = groupMembershipRepository.findAllByGroupId(event.getGroupId());

        Group group = groupRepository.findById(event.getGroupId())
                .orElseThrow(() -> new NoGroupFoundException("No group associated with the groupId"));

        List<String> groupMemberIds = groupMembershipList.stream()
                .map(GroupMembership::getUserProfileId)
                .toList();

        List<String> tokens = userProfileRepository.findByFcmTokenIsNotNull().stream()
                .filter(profile -> groupMemberIds.contains(profile.getId()))
                .map(UserProfile::getFcmToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String title = FirebaseMessageTemplates.NEW_EVENT_TITLE;
        String content = FirebaseMessageTemplates.buildNewEventContent(event, group.getName());

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
