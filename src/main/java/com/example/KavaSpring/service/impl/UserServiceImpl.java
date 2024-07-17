package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;


    @Override
    public List<GetUsersResponse> getAll() {
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
        return users;
    }

    @Override
    public GetUserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        GetUserResponse userResponse = new GetUserResponse();

        userResponse.setUserId(user.getId());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCoffeeNumber(user.getCoffeeNumber());
        userResponse.setScore(user.getScore());

        return userResponse;
    }

    @Override
    public List<CoffeeOrderDto> getOrdersForUser(String id) {

        List<CoffeeOrder> orders = coffeeOrderRepository.findByUserId(id);

        return orders.stream()
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

    }

    @Override
    public BrewEvent getBrewEventsForUser(String id) {
        BrewEvent event = brewEventRepository.findByUserIdAndStatus(id, EventStatus.IN_PROGRESS);

        GetBrewEventHistoryResponse response = new GetBrewEventHistoryResponse();

        response.setOrderIds(event.getOrderIds());

        return event;
    }

    @Override
    public String getEventForOrder(GetEventsForUserRequest request) {
        BrewEvent event = brewEventRepository.findByUserIdAndOrderIdsContaining(request.getUserId(), request.getCoffeeOrderId());
        return event.getEventId();
    }
}
