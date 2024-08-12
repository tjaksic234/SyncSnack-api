package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.EventNotification;
import com.example.KavaSpring.models.dto.OrderNotification;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.services.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private final EventRepository eventRepository;

    @Override
    public void notifyEventUserProfile(Order order) {
        Optional<Event> event = eventRepository.findById(order.getEventId());
        if (event.isEmpty()) {
            throw new IllegalStateException("No event present for the order being placed");
        }
        String userProfileId = event.get().getUserProfileId();

        OrderNotification notification = new OrderNotification();
        notification.setOrderId(order.getId());
        notification.setUserProfileId(order.getUserProfileId());
        notification.setEventId(event.get().getId());
        notification.setDescription("New order was placed for your event");

        log.info("Notifying the event creator");
        messagingTemplate.convertAndSend("/topic/orders/" + userProfileId, notification);
    }

    @Override
    public void notifyGroupMembers(Event event) {

        EventNotification notification = new EventNotification();
        notification.setEventId(event.getId());
        notification.setGroupId(event.getGroupId());
        notification.setDescription(event.getDescription());

        log.info("Notifying the group with a new event");
        messagingTemplate.convertAndSend("/topic/groups/" + event.getGroupId(), notification);
    }
}
