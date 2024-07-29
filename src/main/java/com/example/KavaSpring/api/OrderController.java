package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    @PostMapping("active")
    public ResponseEntity<List<OrderActiveResponse>> activeOrders() {
        try {
            log.info("Fetching active orders for user profile");
            List<OrderActiveResponse> activeOrders = orderService.activeOrders();
            if (activeOrders == null || activeOrders.isEmpty()) {
                log.info("No active orders found for user profile");
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(activeOrders);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

  /*  @PostMapping("completed")
    public ResponseEntity<List<OrderCompletedRequest>> completedOrders() {
        try {
            log.info("Fetching completed orders for user profile");
            return ResponseEntity.ok();
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
*/

}
