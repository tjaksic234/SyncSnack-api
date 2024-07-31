package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.OrderStatus;
import com.example.KavaSpring.services.OrderService;
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
@ShowAPI
public class OrderController {

    private final OrderService orderService;

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


    //TODO dodaj endpoint koji ce azurirat ocjenu od ordera
}
