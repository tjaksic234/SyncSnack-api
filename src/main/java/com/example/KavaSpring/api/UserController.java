package com.example.KavaSpring.api;

import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;

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
        } catch (HttpClientErrorException.Unauthorized e) {
            log.error(String.format(e.getMessage()));
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
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Retrieve all orders for a specific user", description = "Fetches all coffee orders for a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "204", description = "No orders found for the user", content = @Content)
    })
    @GetMapping("{id}/orders")
    public ResponseEntity<List<CoffeeOrderDto>> getOrders(@PathVariable("id") String id) {
        List<CoffeeOrder> orders = coffeeOrderRepository.findByUserId(id);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<CoffeeOrderDto> orderDTOs = orders.stream()
                    .map(order -> new CoffeeOrderDto(
                            order.getCoffeeOrderId(),
                            order.getEventId(),
                            order.getUserId(),
                            order.getType(),
                            order.getSugarQuantity(),
                            order.getMilkQuantity(),
                            order.getRating()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        }
    }

    @Operation(summary = "Retrieve all brew events for a specific user", description = "Fetches all brew events for a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved brew events"),
            @ApiResponse(responseCode = "204", description = "No brew events found for the user", content = @Content)
    })
    @GetMapping("{userId}/events")
    public ResponseEntity<BrewEvent> getBrewEventHistory(@PathVariable("userId") String userId) {
        BrewEvent event = brewEventRepository.findByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);

        GetBrewEventHistoryResponse response = new GetBrewEventHistoryResponse();

        response.setOrderIds(event.getOrderIds());

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @Operation(summary = "Retrieve the event associated with an order", description = "Fetches the event associated with a given coffee order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved event"),
            @ApiResponse(responseCode = "204", description = "No event found for the given order ID", content = @Content)
    })
    @GetMapping("events")
    public ResponseEntity<String> getEventForOrder(@RequestBody GetEventsForUserRequest request) {

        BrewEvent event = brewEventRepository.findByUserIdAndOrderIdsContaining(request.getUserId(), request.getCoffeeOrderId());

        if (event == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(event.getEventId());

    }

}
