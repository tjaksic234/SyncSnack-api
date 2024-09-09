package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.GroupMembership;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.EventNotification;
import com.example.KavaSpring.models.dto.OrderNotification;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.GroupMembershipRepository;
import com.example.KavaSpring.repository.NotificationRepository;
import com.example.KavaSpring.services.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    private final NotificationRepository notificationRepository;

    private final GroupMembershipRepository groupMembershipRepository;

    @Override
    public void notifyEventUserProfile(Order order) {
        Optional<Event> event = eventRepository.findById(order.getEventId());
        if (event.isEmpty()) {
            throw new IllegalStateException("No event present for the order being placed");
        }
        String recipientUserProfileId = event.get().getUserProfileId();

        OrderNotification orderNotification = converterService.convertOrderToOrderNotification(order);

        //? saving the notification to the database
        notificationRepository.save(converterService.convertOrderNotificationToNotification(orderNotification, recipientUserProfileId));

        log.info("Notifying the event creator");
        messagingTemplate.convertAndSend("/topic/orders/" + recipientUserProfileId,
                orderNotification);
    }

    @Override
    public void notifyGroupMembers(Event event) {
        EventNotification eventNotification = converterService.convertEventToEventNotification(event);

        //? saving the notification to the database
        notificationRepository.save(converterService.convertEventNotificationToNotification(eventNotification));

        List<GroupMembership> memberships = groupMembershipRepository.findAllByGroupId(event.getGroupId());

        log.info("Notifying {} members of the group with a new event", memberships.size());
        for (GroupMembership membership : memberships) {
            String userProfileId = membership.getUserProfileId();
            messagingTemplate.convertAndSend("/topic/users/" + userProfileId, eventNotification);
        }
    }
}
