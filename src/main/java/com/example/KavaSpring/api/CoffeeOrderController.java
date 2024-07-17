package com.example.KavaSpring.api;


import com.example.KavaSpring.models.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dto.EditOrderRequest;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.GetOrderResponse;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@Slf4j
public class CoffeeOrderController {

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;

    private final UserRepository userRepository;

    @Autowired
    public CoffeeOrderController(CoffeeOrderRepository coffeeOrderRepository, BrewEventRepository brewEventRepository,
                                 UserRepository userRepository) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.brewEventRepository = brewEventRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Create a coffee order", description = "Creates a new coffee order for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created coffee order"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateCoffeeOrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CoffeeOrder order = new CoffeeOrder (
                request.getUserId(),
                request.getEventId(),
                request.getType(),
                request.getSugarQuantity(),
                request.getMilkQuantity(),
                request.getRating()
        );
        coffeeOrderRepository.save(order);


        BrewEvent event = brewEventRepository.findByEventId(request.getEventId());
        event.getOrderIds().add(order.getCoffeeOrderId());
        brewEventRepository.save(event);

        //* This will update the coffee order counter for the user in the user repository
        user.setCoffeeNumber(user.getCoffeeNumber() + 1);
        userRepository.save(user);

        return new ResponseEntity<>(order.getCoffeeOrderId(), HttpStatus.OK);
    }

    @Operation(summary = "Edit a coffee order", description = "Edits the rating of an existing coffee order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited coffee order"),
            @ApiResponse(responseCode = "404", description = "Coffee order not found", content = @Content)
    })
    @PatchMapping("/edit")
    public ResponseEntity<String> editOrder(@RequestBody EditOrderRequest request) {

        int result = coffeeOrderRepository.updateRating(
                request.getCoffeeOrderId(),
                request.getRatingUpdate()
        );

        if (result == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("Order successfully edited!");
    }

    @Operation(summary = "Get a specific coffee order", description = "Fetches a coffee order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved coffee order"),
            @ApiResponse(responseCode = "400", description = "Invalid coffee order ID", content = @Content)
    })
    @GetMapping("{id}")
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable("id") String coffeeOrderId) {
        CoffeeOrder order = coffeeOrderRepository.findByCoffeeOrderId(coffeeOrderId);

        if (order == null) {
            log.error("Bad coffeeOrder object returned!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while processing the request");
        }

        GetOrderResponse response = new GetOrderResponse();
        response.setCoffeeOrderId(order.getCoffeeOrderId());
        response.setType(order.getType());
        response.setSugarQuantity(order.getSugarQuantity());
        response.setMilkQuantity(order.getMilkQuantity());


        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Retrieve all coffee orders", description = "Fetches all coffee orders from the repository")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of coffee orders")
    })
    @GetMapping
    public ResponseEntity<List<CoffeeOrder>> getCoffeeOrders() {

        List<CoffeeOrder> types = coffeeOrderRepository.findAll();

        return new ResponseEntity<>(types, HttpStatus.OK);
    }
}
