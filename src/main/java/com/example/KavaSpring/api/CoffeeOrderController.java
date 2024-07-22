/*
package com.example.KavaSpring.api;


import com.example.KavaSpring.exceptions.EmptyContentException;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dto.EditOrderRequest;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dto.GetOrderResponse;
import com.example.KavaSpring.service.CoffeeOrderService;
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
@RequestMapping("api/orders")
@Slf4j
@AllArgsConstructor
public class CoffeeOrderController {

    private final CoffeeOrderService coffeeOrderService;

    @Operation(summary = "Create a coffee order", description = "Creates a new coffee order for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created coffee order"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateCoffeeOrderRequest request) {

        try {
            return new ResponseEntity<>(coffeeOrderService.create(request), HttpStatus.OK);
        } catch (UnauthorizedException | EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Edit a coffee order", description = "Edits the rating of an existing coffee order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully edited coffee order"),
            @ApiResponse(responseCode = "404", description = "Coffee order not found", content = @Content)
    })
    @PatchMapping("/edit")
    public ResponseEntity<String> editOrder(@RequestBody EditOrderRequest request) {

       try {
           return new ResponseEntity<>(coffeeOrderService.editOrder(request), HttpStatus.OK);
       } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
       }
    }

    @Operation(summary = "Get a specific coffee order", description = "Fetches a coffee order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved coffee order"),
            @ApiResponse(responseCode = "400", description = "Invalid coffee order ID", content = @Content)
    })
    @GetMapping("{id}")
    public ResponseEntity<GetOrderResponse> getOrderById(@PathVariable("id") String coffeeOrderId) {
        try {
            return new ResponseEntity<>(coffeeOrderService.getOrderById(coffeeOrderId), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Retrieve all coffee orders", description = "Fetches all coffee orders from the repository")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of coffee orders")
    })
    @GetMapping
    public ResponseEntity<List<CoffeeOrder>> getCoffeeOrders() {
        try {
            return new ResponseEntity<>(coffeeOrderService.getCoffeeOrders(), HttpStatus.OK);
        } catch (EmptyContentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
*/
