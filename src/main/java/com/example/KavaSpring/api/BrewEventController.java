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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "Create a brew event", description = "Creates a new brew event for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created brew event"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already has a brewing event in progress", content = @Content)
    })
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


    @Operation(summary = "Complete a brew event", description = "Changes the status of an in-progress brew event to completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully completed brew event"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "No active brewing events in progress", content = @Content)
    })
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


    @Operation(summary = "Get pending brew events", description = "Aggregates data to return all pending brew events except for the user making the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending brew events")
    })
    @GetMapping("/pending/{id}")
    public ResponseEntity<List<BrewEventResult>> getPendingEvents(@PathVariable("id") String userId) {
        BrewEventAggregationServiceImpl aggregation = new BrewEventAggregationServiceImpl(mongoTemplate);
        aggregation.setId(new ObjectId(userId).toString());
        List<BrewEventResult> results = aggregation.aggregateBrewEvents();
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Get brew events by status", description = "Fetches all brew events based on their status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of brew events")
    })
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

    @Operation(summary = "Get a specific brew event by ID", description = "Fetches a brew event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved brew event"),
            @ApiResponse(responseCode = "404", description = "Brew event not found", content = @Content)
    })
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
