package com.example.KavaSpring.api;

import com.example.KavaSpring.models.dto.CreateBrewEventRequest;
import com.example.KavaSpring.models.dto.GetBrewEventsResponse;
import com.example.KavaSpring.service.BrewEventAggregationService;
import com.example.KavaSpring.service.impl.BrewEventAggregationServiceImpl;
import com.example.KavaSpring.models.dto.BrewEventResult;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/events")
@Slf4j
public class BrewEventController {

    private final BrewEventRepository brewEventRepository;

    private final UserRepository userRepository;

    private final BrewEventAggregationService brewEventAggregationService;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BrewEventController(BrewEventRepository brewEventRepository, UserRepository userRepository,
                               BrewEventAggregationService brewEventAggregationService, MongoTemplate mongoTemplate) {
        this.brewEventRepository = brewEventRepository;
        this.userRepository = userRepository;
        this.brewEventAggregationService = brewEventAggregationService;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateBrewEventRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByUserIdAndStatus(user.getId(), EventStatus.PENDING)
                || brewEventRepository.existsByUserIdAndStatus(user.getId(), EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already has a brewing event in progress.");
        }


        BrewEvent event = new BrewEvent(user.getId(), request.getPendingTime());
        brewEventRepository.save(event);

        return new ResponseEntity<>(event.getEventId(), HttpStatus.OK);
    }


    //* this will change the status of the event into COMPLETE and mark it finished
    @PatchMapping("complete-event")
    public ResponseEntity<String> finishBrewEvent(@RequestParam(name = "userId") String userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            BrewEvent event = brewEventRepository.findByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);
            event.setStatus(EventStatus.COMPLETED);
            brewEventRepository.save(event);
        } else {
            return new ResponseEntity<>("The user has no active brewing events IN_PROGRESS!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("The brew event has been successfully altered", HttpStatus.OK);
    }


    //* This will aggregate data to return all the pending brew events except for the user that is calling this endpoint
    @GetMapping("/pending/{id}")
    public ResponseEntity<List<BrewEventResult>> getPendingEvents(@PathVariable("id") String userId) {
        BrewEventAggregationServiceImpl aggregation = new BrewEventAggregationServiceImpl(mongoTemplate);
        aggregation.setId(new ObjectId(userId).toString());
        List<BrewEventResult> results = aggregation.aggregateBrewEvents();
        return ResponseEntity.ok(results);
    }

    //* Retrieve all events based on their event status enums
    @GetMapping()
    public ResponseEntity<List<GetBrewEventsResponse>> getEvents(@RequestParam(name = "status", defaultValue = "PENDING") EventStatus status) {
        List<BrewEvent> events = brewEventRepository.findByStatus(status);

        List<GetBrewEventsResponse> responseList = events.stream()
                .map(event -> {
                    GetBrewEventsResponse response = new GetBrewEventsResponse();
                    response.setEventId(event.getEventId());
                    response.setUserId(event.getUserId());
                    response.setStatus(event.getStatus());
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    //* Get a specific event by id
    @GetMapping("{id}")
    public ResponseEntity<BrewEvent> getEvent(@PathVariable("id") String id) {
        try {
            BrewEvent event = brewEventRepository.findByEventId(id);

            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            log.error("There was an error fetching an event by ID", HttpStatus.NOT_FOUND);
            throw new RuntimeException(e);
        }
    }


}
