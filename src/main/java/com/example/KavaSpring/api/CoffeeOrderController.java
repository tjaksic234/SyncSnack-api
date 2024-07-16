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

    @GetMapping
    public ResponseEntity<List<CoffeeOrder>> getCoffeeOrders() {

        List<CoffeeOrder> types = coffeeOrderRepository.findAll();

        return new ResponseEntity<>(types, HttpStatus.OK);
    }
}
