package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;

import java.util.List;

public interface EventService {
    EventResponse createEvent(String groupId, EventRequest request);
    EventExpandedResponse getEventById(String id);
    List<EventExpandedResponse> filterEvents(String groupId, EventSearchRequest request);
    void updateEventsJob();
    String updateEventStatus(String id, EventStatus status);
    EventDto getActiveEvent();
}
