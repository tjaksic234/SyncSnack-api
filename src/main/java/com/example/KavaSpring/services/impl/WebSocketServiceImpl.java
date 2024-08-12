package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;
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

    private final ConverterService converterService;

    @Override
    public void notifyEventUserProfile(Order order) {
        Optional<Event> event = eventRepository.findById(order.getEventId());
        if (event.isEmpty()) {
            throw new IllegalStateException("No event present for the order being placed");
        }
        String userProfileId = event.get().getUserProfileId();

        log.info("Notifying the event creator");
        messagingTemplate.convertAndSend("/topic/orders/" + userProfileId,
                converterService.convertOrderToOrderNotification(order));
    }

    @Override
    public void notifyGroupMembers(Event event) {
        log.info("Notifying the group with a new event");
        messagingTemplate.convertAndSend("/topic/groups/" + event.getGroupId(),
                converterService.convertEventToEventNotification(event));
    }
}
