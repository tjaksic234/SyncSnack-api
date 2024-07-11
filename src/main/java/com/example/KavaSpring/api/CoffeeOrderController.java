package com.example.KavaSpring.api;


import com.example.KavaSpring.api.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.CoffeeType;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class CoffeeOrderController {


    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    @PostMapping("create")
    public ResponseEntity<String> create(@RequestBody CreateCoffeeOrderRequest request) {

       /* if (coffeeOrderRepository.existsByCoffeeOrderId(request.getCoffeeOrderId())) {
            return new ResponseEntity<>("The coffee order already exists" ,HttpStatus.BAD_REQUEST);
        }*/

        CoffeeOrder order = new CoffeeOrder (
                request.getType(),
                request.getSugarQuantity(),
                request.getMilkQuantity()
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
