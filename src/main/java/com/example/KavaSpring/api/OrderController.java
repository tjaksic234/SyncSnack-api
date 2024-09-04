package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.OrderAlreadyRatedException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.OrderStatus;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.PageRequest;
import java.util.List;

@RestController
@RequestMapping("api/orders")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class OrderController {

    private final OrderService orderService;

    @PostMapping("create")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader String groupId,
            @RequestBody OrderRequest request
    ) {
        try {
            log.info("Creating order");
            return ResponseEntity.ok(orderService.createOrder(groupId, request));
        } catch (NotFoundException | NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") String id) {
        try {
            log.info("Fetching order by id");
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<OrderEventInfoDto>> getAllOrdersFromUserProfile (
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "0") int rating,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "ALL") EventType eventType,
            @RequestParam(required = false) String search
    ) {
        try {
            log.info("Fetching all orders from user profile");
            List<OrderEventInfoDto> orders = orderService.getAllOrdersFromUserProfile(groupId, PageRequest.of(page, size),
                    rating, status, eventType, search);
            return ResponseEntity.ok(orders);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("activity")
    public ResponseEntity<List<OrderActivityResponse>> getOrdersByActivityStatus(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam boolean isActive) {
        try {
            log.info("Fetching active orders for user profile");
            List<OrderActivityResponse> activeOrders = orderService.getOrdersByActivityStatus(groupId, isActive);
            if (activeOrders == null || activeOrders.isEmpty()) {
                log.info("No active orders found for user profile");
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(activeOrders);
            }
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("update")
    public ResponseEntity<String> updateOrdersStatus(@RequestParam String orderId, @RequestParam OrderStatus status) {
        try {
            log.info("Order status update started");
            return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("event/{eventId}")
    public ResponseEntity<List<OrderExpandedResponse>> getActiveOrdersByEventId(@PathVariable String eventId) {
        try {
            log.info("Fetching orders by eventId");
            return ResponseEntity.ok(orderService.getActiveOrdersByEventId(eventId));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("rate")
    public ResponseEntity<String> rateOrder(@RequestParam String orderId, @RequestParam int rating) {
        try {
            log.info("Order rating started");
            return ResponseEntity.ok(orderService.rateOrder(orderId, rating));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (OrderAlreadyRatedException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
