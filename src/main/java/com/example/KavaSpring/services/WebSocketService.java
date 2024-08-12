package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Order;

public interface WebSocketService {
    void notifyEventUserProfile(Order order);
    void notifyGroupMembers(Event event);
}
