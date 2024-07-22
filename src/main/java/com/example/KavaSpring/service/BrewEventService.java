package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.enums.OrderStatus;

import java.util.List;

public interface BrewEventService {

    String create(CreateBrewEventRequest request);
    String finishBrewEvent(String userId);
    List<GetBrewEventsResponse> getEventsByStatus(OrderStatus status);
    BrewEvent getEventById(String id);
    List<BrewEventResult> getPendingEvents(String id);
}
