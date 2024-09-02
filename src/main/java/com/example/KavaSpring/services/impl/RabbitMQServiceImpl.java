package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.config.RabbitMQConfig;
import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dto.EventNotification;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.repository.NotificationRepository;
import com.example.KavaSpring.services.RabbitMQService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class RabbitMQServiceImpl implements RabbitMQService {

    private final RabbitTemplate rabbitTemplate;

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    private final NotificationRepository notificationRepository;

    @Override
    public void notifyGroupMembers(Event event) {
        String routingKey = "event." + event.getGroupId();
        EventNotification eventNotification = converterService.convertEventToEventNotification(event);

        log.warn("Notifying the group with a new event through rabbitMQ");
        rabbitTemplate.convertAndSend(RabbitMQConfig.TOPIC_EXCHANGE_NAME, routingKey, eventNotification);
    }
}
