package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Event;

public interface RabbitMQService {
    void notifyGroupMembers(Event event);
}
