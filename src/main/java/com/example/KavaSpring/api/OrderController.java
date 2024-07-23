package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.OrderDto;
import com.example.KavaSpring.models.dto.OrderRequest;
import com.example.KavaSpring.models.dto.OrderResponse;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/orders")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        try {
            log.info("Creating order");
            return ResponseEntity.ok(orderService.createOrder(request));
        } catch (UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable("id") String id) {
        try {
            log.info("Fetching order by id");
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
