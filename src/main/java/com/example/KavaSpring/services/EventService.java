package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dto.EventDto;
import com.example.KavaSpring.models.dto.EventRequest;
import com.example.KavaSpring.models.dto.EventResponse;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;

import java.util.List;

public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventDto getEventById(String id);
    List<EventDto> searchEvents(EventStatus status, EventRequest request);
}
