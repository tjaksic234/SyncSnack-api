package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.EventAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dto.EventDto;
import com.example.KavaSpring.models.dto.EventRequest;
import com.example.KavaSpring.models.dto.EventResponse;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.EventRepository;
import com.example.KavaSpring.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ConverterService converterService;

    @Override
    public EventResponse createEvent(EventRequest request) {
        //? Logiku provjere eventova za usera ce trebati popraviti jer creator moze imati samo jedan event nebitno jeli completed,
        //? pending ili inprogress pa treba jos poraditi na logici

        if (eventRepository.findAll().isEmpty()) {
            log.info("There are no events associated with the creatorId: {}, so the event creation continues", request.getCreatorId());
        } else {
            throw new EventAlreadyExistsException();
        }

        Event event = new Event();
        event.setCreatorId(request.getCreatorId());
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setGroupId(request.getGroupId());
        event.setEventType(request.getEventType());
        eventRepository.save(event);

        log.info("Event created");
        return converterService.convertToEventResponse(request);
    }

    @Override
    public EventDto getEventById(String id) {
        Event event = eventRepository.getById(id).orElseThrow(() -> new NotFoundException("No event associated with the id"));
        log.info("Get event by id finished");
        return converterService.convertToEventDto(event);
    }
}
