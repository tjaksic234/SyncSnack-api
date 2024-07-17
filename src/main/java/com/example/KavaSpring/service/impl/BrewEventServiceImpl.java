package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.exceptions.BadRequestException;
import com.example.KavaSpring.exceptions.BrewEventAlreadyExistsException;
import com.example.KavaSpring.exceptions.EmptyContentException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.BrewEventResult;
import com.example.KavaSpring.models.dto.CreateBrewEventRequest;
import com.example.KavaSpring.models.dto.GetBrewEventsResponse;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.BrewEventAggregationService;
import com.example.KavaSpring.service.BrewEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BrewEventServiceImpl implements BrewEventService {

    private final UserRepository userRepository;

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;

    private final BrewEventAggregationService brewEventAggregationService;

    private MongoTemplate mongoTemplate;

    @Override
    public String create(CreateBrewEventRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByUserIdAndStatus(user.getId(), EventStatus.PENDING)
                || brewEventRepository.existsByUserIdAndStatus(user.getId(), EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            throw new BrewEventAlreadyExistsException("The user already has an event PENDING or IN PROGRESS");
        }


        BrewEvent event = new BrewEvent(user.getId(), request.getPendingTime());
        brewEventRepository.save(event);

        return event.getEventId();
    }

    @Override
    public String finishBrewEvent(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            BrewEvent event = brewEventRepository.findByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);
            event.setStatus(EventStatus.COMPLETED);
            brewEventRepository.save(event);
        } else {
            throw new BadRequestException("The user has no active brewing events IN_PROGRESS!");
        }
        return "The brew event has been successfully altered";
    }

    @Override
    public List<GetBrewEventsResponse> getEventsByStatus(EventStatus status) {
        List<BrewEvent> events = brewEventRepository.findByStatus(status);

        return events.stream()
                .map(event -> {
                    GetBrewEventsResponse response = new GetBrewEventsResponse();
                    response.setEventId(event.getEventId());
                    response.setUserId(event.getUserId());
                    response.setStatus(event.getStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BrewEvent getEventById(String id) {
        BrewEvent event = brewEventRepository.findByEventId(id);
        if (event == null) {
            throw new NotFoundException("Brew event not found for ID: " + id);
        }
        return event;
    }

    @Override
    public List<BrewEventResult> getPendingEvents(String id) {

        BrewEventAggregationServiceImpl aggregation = new BrewEventAggregationServiceImpl(mongoTemplate);

        aggregation.setId(new ObjectId(id).toString());

        List<BrewEventResult> results = aggregation.aggregateBrewEvents();

        if (results == null || results.isEmpty()) {
            throw new EmptyContentException("No pending events found for user: " + id);
        }

        return results;
    }
}
