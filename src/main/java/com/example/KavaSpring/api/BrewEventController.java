package com.example.KavaSpring.api;

import com.example.KavaSpring.api.dto.CreateBrewEventRequest;
import com.example.KavaSpring.api.dto.CompleteBrewEventRequest;
import com.example.KavaSpring.helper.BrewEventAggregation;
import com.example.KavaSpring.helper.dto.BrewEventResult;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.enums.EventStatus;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/events")
public class BrewEventController {

    @Autowired
    private BrewEventRepository brewEventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrewEventAggregation brewEventAggregation;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateBrewEventRequest request) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByCreator_IdAndStatus(creator.getId(),
                 EventStatus.PENDING);

        if (hasActiveEvent) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already has a PENDING brewing event in progress.");
        }


        BrewEvent newBrewEvent = new BrewEvent(creator, request.getPendingTime());
        BrewEvent savedBrewEvent = brewEventRepository.save(newBrewEvent);

        return new ResponseEntity<>("Event successfully created!", HttpStatus.OK);
    }

    @PatchMapping("complete-event")
    public ResponseEntity<String> editBrewEvent(@RequestBody CompleteBrewEventRequest request) {

        userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByCreator_IdAndStatus(request.getCreatorId(), EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            BrewEvent event = brewEventRepository.findByCreatorIdAndEventId(request.getCreatorId(), request.getEventId());
            event.setStatus(EventStatus.COMPLETED);
            brewEventRepository.save(event);
        } else {
            return new ResponseEntity<>("The user has no active brewing events IN_PROGRESS!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("The brew event has been successfully altered", HttpStatus.OK);
    }

    @GetMapping("/ongoing/{id}")
    public ResponseEntity<List<BrewEventResult>> pending(@PathVariable("id") String id) {
        BrewEventAggregation aggregation = new BrewEventAggregation(mongoTemplate);
        aggregation.setId(id);
        List<BrewEventResult> results = aggregation.aggregateBrewEvents();
        return ResponseEntity.ok(results);
    }

    @GetMapping("inProgress")
    public ResponseEntity<List<BrewEvent>> inProgress(@RequestParam(name = "status", defaultValue = "IN_PROGRESS") EventStatus status) {
        List<BrewEvent> events = brewEventRepository.findByStatus(status);
        return ResponseEntity.ok(events);
    }

}
