package com.example.KavaSpring.api;

import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dto.CreateBrewEventRequest;
import com.example.KavaSpring.models.dto.GetBrewEventsResponse;
import com.example.KavaSpring.service.BrewEventService;
import com.example.KavaSpring.models.dto.BrewEventResult;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.enums.EventStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/events")
@Slf4j
@AllArgsConstructor
public class BrewEventController {

    private final BrewEventService brewEventService;

    @Operation(summary = "Create a brew event", description = "Creates a new brew event for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created brew event"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already has a brewing event in progress", content = @Content)
    })
    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateBrewEventRequest request) {

        try {
            return new ResponseEntity<>(brewEventService.create(request),HttpStatus.OK);
        } catch (BrewEventAlreadyExistsException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "Complete a brew event", description = "Changes the status of an in-progress brew event to completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully completed brew event"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "No active brewing events in progress", content = @Content)
    })
    @PatchMapping("complete-event")
    public ResponseEntity<String> finishBrewEvent(@RequestParam(name = "userId") String userId) {

        try {
            return new ResponseEntity<>(brewEventService.finishBrewEvent(userId), HttpStatus.OK);
        } catch (BadRequestException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Get pending brew events", description = "Aggregates data to return all pending brew events except for the user making the request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved pending brew events")
    })
    @GetMapping("/pending/{id}")
    public ResponseEntity<List<BrewEventResult>> getPendingEvents(@PathVariable("id") String userId) {
        try {
            return new ResponseEntity<>(brewEventService.getPendingEvents(userId), HttpStatus.OK);
        } catch (NotFoundException | EmptyContentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get brew events by status", description = "Fetches all brew events based on their status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of brew events")
    })
    @GetMapping()
    public ResponseEntity<List<GetBrewEventsResponse>> getEventsByStatus(@RequestParam(name = "status", defaultValue = "COMPLETED") EventStatus status) {
        try {
            return new ResponseEntity<>(brewEventService.getEventsByStatus(status), HttpStatus.OK);
        } catch (EmptyContentException | NotValidEnumException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get a specific brew event by ID", description = "Fetches a brew event by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved brew event"),
            @ApiResponse(responseCode = "404", description = "Brew event not found", content = @Content)
    })
    @GetMapping("{id}")
    public ResponseEntity<BrewEvent> getEventById(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(brewEventService.getEventById(id),HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
