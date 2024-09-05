package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.services.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/events")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class EventController {

    private final EventService eventService;

    @PostMapping("create")
    public ResponseEntity<EventResponse> createEvent(@RequestHeader(value = "groupId") String groupId, @RequestBody EventRequest request) {
        try {
            log.info("Create a event requested");
            return ResponseEntity.ok(eventService.createEvent(groupId, request));
        } catch (EventAlreadyExistsException | IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException | NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<EventExpandedResponse> getEventById(@PathVariable("id") String id) {
        try {
            log.info("Fetching event by id");
            return ResponseEntity.ok(eventService.getEventById(id));
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("filter")
    public ResponseEntity<List<EventExpandedResponse>> filterEvents(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(required = false) String search,
            @RequestBody EventSearchRequest request
    ) {
        try {
            log.info("Search for events started");
            return ResponseEntity.ok(eventService.filterEvents(groupId, PageRequest.of(page, size), search, request));
        } catch (NotValidEnumException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("update")
    public ResponseEntity<String> updateEventStatus(@RequestParam String eventId, @RequestParam EventStatus status) {
        try {
            log.info("Event status update started");
            return ResponseEntity.ok(eventService.updateEventStatus(eventId, status));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (NotValidEnumException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("active")
    public ResponseEntity<?> getActiveEvent(@RequestHeader(value = "groupId") String groupId) {
        try {
            log.info("Fetching active event from UserProfile");
            return ResponseEntity.ok(eventService.getActiveEvent(groupId));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

}
