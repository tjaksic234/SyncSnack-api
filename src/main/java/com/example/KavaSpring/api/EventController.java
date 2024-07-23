package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.EventAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.EventDto;
import com.example.KavaSpring.models.dto.EventRequest;
import com.example.KavaSpring.models.dto.EventResponse;
import com.example.KavaSpring.services.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/events")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventRequest request) {
        try {
            log.info("Create a event requested");
            return ResponseEntity.ok(eventService.createEvent(request));
        } catch (EventAlreadyExistsException | NullPointerException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEventById(@PathVariable("id") String id) {
        try {
            log.info("Fetching event by id");
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (UnauthorizedException | NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
