package com.example.KavaSpring.api;

import com.example.KavaSpring.api.dto.CreateBrewEventRequest;
import com.example.KavaSpring.api.dto.EditBrewEventRequest;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.EventStatus;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/events")
public class BrewEventController {

    @Autowired
    private BrewEventRepository brewEventRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateBrewEventRequest request) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByCreator_IdAndStatus(creator.getId(), EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User already has an active brewing event in progress.");
        }


        BrewEvent newBrewEvent = new BrewEvent(creator, request.getEndTime());
        BrewEvent savedBrewEvent = brewEventRepository.save(newBrewEvent);

        return new ResponseEntity<>("Event successfully created!", HttpStatus.OK);
    }

    @PatchMapping("edit")
    public ResponseEntity<String> editBrewEvent(@RequestBody EditBrewEventRequest request) {

        userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean hasActiveEvent = brewEventRepository.existsByCreator_IdAndStatus(request.getCreatorId(), EventStatus.IN_PROGRESS);

        if (hasActiveEvent) {
            BrewEvent event = brewEventRepository.findByCreatorIdAndEventId(request.getCreatorId(), request.getEventId());
            event.setStatus(request.getEventStatus());
            brewEventRepository.save(event);
        } else {
            return new ResponseEntity<>("The user has no active brewing events present!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("The brew event has been successfully altered", HttpStatus.OK);
    }

}
