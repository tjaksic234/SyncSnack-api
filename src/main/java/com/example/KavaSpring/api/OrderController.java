package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.NotValidEnumException;
import com.example.KavaSpring.exceptions.OrderAlreadyRatedException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.OrderStatus;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class OrderController {

    private final OrderService orderService;

    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        try {
            log.info("Creating order");
            return ResponseEntity.ok(orderService.createOrder(request));
        } catch (NotFoundException e) {
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
    public ResponseEntity<List<OrderEventInfoDto>> getAllOrdersFromUserProfile() {
        try {
            log.info("Fetching all orders from user profile");
            List<OrderEventInfoDto> orders = orderService.getAllOrdersFromUserProfile();

            if (orders.isEmpty()) {
                log.info("No orders found for user profile id");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(orders);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("activity")
    public ResponseEntity<List<OrderActivityResponse>> getOrdersByActivityStatus(@RequestParam boolean isActive) {
        try {
            log.info("Fetching active orders for user profile");
            List<OrderActivityResponse> activeOrders = orderService.getOrdersByActivityStatus(isActive);
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

    @PatchMapping("update/all")
    public ResponseEntity<String> updateAllOrdersStatus(@RequestParam String eventId, @RequestParam OrderStatus status) {
        try {
            log.info("All orders status update started");
            return ResponseEntity.ok(orderService.updateAllOrdersStatus(eventId, status));
        } catch (NotValidEnumException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
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

    @PostMapping("search")
    public ResponseEntity<List<OrderSearchResponse>> searchOrders(@RequestBody OrderSearchRequest request) {
        try {
            log.info("Search orders");
            return ResponseEntity.ok(orderService.searchOrders(request));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
