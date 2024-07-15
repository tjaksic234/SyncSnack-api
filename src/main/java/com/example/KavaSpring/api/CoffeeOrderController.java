package com.example.KavaSpring.api;


import com.example.KavaSpring.api.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.api.dto.EditOrderRequest;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class CoffeeOrderController {


    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    @Autowired
    private BrewEventRepository brewEventRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateCoffeeOrderRequest request) {

        User creator = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CoffeeOrder order = new CoffeeOrder (
                creator,
                request.getType(),
                request.getSugarQuantity(),
                request.getMilkQuantity(),
                request.getRating()
        );

        BrewEvent event = brewEventRepository.findByEventId(request.getEventId());
        event.getOrders().add(order);

        coffeeOrderRepository.save(order);
        brewEventRepository.save(event);

        return new ResponseEntity<>("Order successfully created", HttpStatus.OK);
    }

    @PatchMapping("/edit")
    public ResponseEntity<String> editOrder(@RequestBody EditOrderRequest request) {

        CoffeeOrder order = coffeeOrderRepository.findById(request.getCoffeeOrderId())
                .orElseThrow(() -> new NullPointerException("Order not found!"));

        order.setRating(request.getRatingUpdate());
        coffeeOrderRepository.save(order);


        BrewEvent event = brewEventRepository.findByEventId(request.getEventId());

        for (CoffeeOrder embeddedOrder : event.getOrders()) {
            if (embeddedOrder.getCoffeeOrderId().equals(request.getCoffeeOrderId())) {
                embeddedOrder.setRating(request.getRatingUpdate());
                break;
            }
        }
        brewEventRepository.save(event);


        return ResponseEntity.ok("Order successfully edited!");

    }



    @GetMapping
    public ResponseEntity<List<CoffeeOrder>> getCoffeeOrders() {

        List<CoffeeOrder> types = coffeeOrderRepository.findAll();

        return new ResponseEntity<>(types, HttpStatus.OK);
    }
}
