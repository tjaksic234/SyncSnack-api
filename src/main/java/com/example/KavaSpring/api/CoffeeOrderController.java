package com.example.KavaSpring.api;


import com.example.KavaSpring.api.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.User;
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

        coffeeOrderRepository.save(order);

        return new ResponseEntity<>("Order successfully created", HttpStatus.OK);
    }



    @GetMapping
    public ResponseEntity<List<CoffeeOrder>> getCoffeeOrders() {

        List<CoffeeOrder> types = coffeeOrderRepository.findAll();

        return new ResponseEntity<>(types, HttpStatus.OK);
    }
}
