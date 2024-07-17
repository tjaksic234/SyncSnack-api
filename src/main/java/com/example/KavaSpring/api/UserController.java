package com.example.KavaSpring.api;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;


    @Autowired
    public UserController(UserRepository userRepository, CoffeeOrderRepository coffeeOrderRepository,
                          BrewEventRepository brewEventRepository) {
        this.userRepository = userRepository;
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.brewEventRepository = brewEventRepository;
    }


    //* Retrieve all users
    @GetMapping
    public ResponseEntity<List<GetUsersResponse>> getAll() {

        List<GetUsersResponse> users = userRepository.findAll()
                .stream().map(user -> {
                    GetUsersResponse response = new GetUsersResponse();
                    response.setEmail(user.getEmail());
                    response.setFirstName(user.getFirstName());
                    response.setLastName(user.getLastName());
                    response.setCoffeeCounter(user.getCoffeeNumber());
                    //response.setCoffeeRating(Float.parseFloat(String.format("%.2f", user.getScore())));
                    response.setCoffeeRating(user.getScore());
                    return response;
                })
                .toList();

        if (users.isEmpty()) {
            ResponseEntity.status(HttpStatus.OK).body("The user collection is empty.");
        }


        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    //* Get a specific user with his id
    @GetMapping("{id}")
    public ResponseEntity<GetUserResponse> getUser(@PathVariable("id") String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GetUserResponse userResponse = new GetUserResponse();

        userResponse.setUserId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCoffeeNumber(user.getCoffeeNumber());
        userResponse.setScore(user.getScore());


        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    //* This retrieves all the orders associated with the user that called this method
    @GetMapping("{id}/orders")
    public ResponseEntity<List<CoffeeOrderDto>> getOrders(@PathVariable("id") String id) {
        List<CoffeeOrder> orders = coffeeOrderRepository.findByUserId(id);

        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            List<CoffeeOrderDto> orderDTOs = orders.stream()
                    .map(order -> new CoffeeOrderDto(
                            order.getCoffeeOrderId(),
                            order.getEventId(),
                            order.getUserId(),
                            order.getType(),
                            order.getSugarQuantity(),
                            order.getMilkQuantity(),
                            order.getRating()
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(orderDTOs);
        }
    }

    //* Retrieve all brew events of the specific user that called this method
    @GetMapping("{userId}/events")
    public ResponseEntity<BrewEvent> getBrewEventHistory(@PathVariable("userId") String userId) {
        BrewEvent event = brewEventRepository.findByUserIdAndStatus(userId, EventStatus.IN_PROGRESS);

        GetBrewEventHistoryResponse response = new GetBrewEventHistoryResponse();

        response.setOrderIds(event.getOrderIds());

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    //* This retrieves the event that is associated with the order id that is in the request body
    @GetMapping("events")
    public ResponseEntity<String> getEventForOrder(@RequestBody GetEventsForUserRequest request) {

        BrewEvent event = brewEventRepository.findByUserIdAndOrderIdsContaining(request.getUserId(), request.getCoffeeOrderId());

        if (event == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(event.getEventId());

    }

}
