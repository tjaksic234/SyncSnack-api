package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.EventDto;
import com.example.KavaSpring.models.dto.EventRequest;
import com.example.KavaSpring.models.dto.EventResponse;

public interface EventService {
    EventResponse createEvent(EventRequest request);
    EventDto getEventById(String id);
}
