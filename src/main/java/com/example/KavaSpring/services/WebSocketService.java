package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.OrderMessage;

public interface WebSocketService {
    void notifyEventUserProfile(Order order);
}
