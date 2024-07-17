package com.example.KavaSpring.api;

import com.example.KavaSpring.exceptions.EmptyContentException;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.service.UserService;
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
@RequestMapping("api/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Retrieve all users", description = "Fetches all users from the repository")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "204", description = "The user collection is empty", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<GetUsersResponse>> getAll() {
        try {
            log.info("Getting all users");
            return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Get a specific user by ID", description = "Fetches a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Retrieve all orders for a specific user", description = "Fetches all coffee orders for a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "204", description = "No orders found for the user", content = @Content)
    })
    @GetMapping("{id}/orders")
    public ResponseEntity<List<CoffeeOrderDto>> getOrdersForUser(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(userService.getOrdersForUser(id), HttpStatus.OK);
        } catch (EntityNotFoundException | EmptyContentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Operation(summary = "Retrieve all brew events for a specific user that are of status IN_PROGRESS", description = "Fetches all brew events that are of status IN_PROGRESS for a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved brew events"),
            @ApiResponse(responseCode = "204", description = "No brew events found for the user", content = @Content)
    })
    @GetMapping("{userId}/events")
    public ResponseEntity<BrewEvent> getBrewEventsForUser(@PathVariable("userId") String userId) {
        try {
            return new ResponseEntity<>(userService.getBrewEventsForUser(userId), HttpStatus.OK);
        } catch (NullPointerException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Retrieve the event associated with an order", description = "Fetches the event associated with a given coffee order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved event"),
            @ApiResponse(responseCode = "204", description = "No event found for the given order ID", content = @Content)
    })
    @GetMapping("events")
    public ResponseEntity<String> getEventForOrder(@RequestBody GetEventsForUserRequest request) {

        try {
            return new ResponseEntity<>(userService.getEventForOrder(request), HttpStatus.OK);
        } catch (NullPointerException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
