package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dto.BrewEventResult;
import com.example.KavaSpring.models.dto.CreateBrewEventRequest;
import com.example.KavaSpring.models.dto.GetBrewEventsResponse;
import com.example.KavaSpring.models.enums.EventStatus;

import java.util.List;

public interface BrewEventService {

    String create(CreateBrewEventRequest request);
    String finishBrewEvent(String userId);
    List<GetBrewEventsResponse> getEventsByStatus(EventStatus status);
    BrewEvent getEventById(String id);
    List<BrewEventResult> getPendingEvents(String id);
}
