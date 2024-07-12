package com.example.KavaSpring.api;

import com.example.KavaSpring.api.dto.GetUsersResponse;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeOrderRepository coffeeOrderRepository;

    @GetMapping
    public ResponseEntity<List<GetUsersResponse>> getAll() {

        List<GetUsersResponse> users = userRepository.getAllBy();

        if (users.isEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body("The user collection is empty.");
        }


        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<List<CoffeeOrder>> getOrdersForUser(@PathVariable("id") String id) {
        List<CoffeeOrder> orders = coffeeOrderRepository.findByCreatorId(id);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(orders);
        }
    }



}
