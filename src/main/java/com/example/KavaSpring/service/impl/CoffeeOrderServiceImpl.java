package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dto.EditOrderRequest;
import com.example.KavaSpring.models.dto.GetOrderResponse;
import com.example.KavaSpring.repository.BrewEventRepository;
import com.example.KavaSpring.repository.CoffeeOrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.CoffeeOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class CoffeeOrderServiceImpl implements CoffeeOrderService {

    private final UserRepository userRepository;

    private final CoffeeOrderRepository coffeeOrderRepository;

    private final BrewEventRepository brewEventRepository;

    @Override
    public String create(CreateCoffeeOrderRequest request) {
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

        return order.getCoffeeOrderId();
    }

    @Override
    public String editOrder(EditOrderRequest request) {
        int result = coffeeOrderRepository.updateRating(
                request.getCoffeeOrderId(),
                request.getRatingUpdate()
        );

        if (result == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coffee order not found or could not be updated.");
        }

        return "Order successfully edited!";
    }

    @Override
    public GetOrderResponse getOrderById(String id) {
        CoffeeOrder order = coffeeOrderRepository.findByCoffeeOrderId(id);

        if (order == null) {
            log.error("Bad coffeeOrder object returned!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while processing the request");
        }

        GetOrderResponse response = new GetOrderResponse();
        response.setCoffeeOrderId(order.getCoffeeOrderId());
        response.setUserId(order.getUserId());
        response.setType(order.getType());
        response.setSugarQuantity(order.getSugarQuantity());
        response.setMilkQuantity(order.getMilkQuantity());


        return response;
    }

    @Override
    public List<CoffeeOrder> getCoffeeOrders() {
        return coffeeOrderRepository.findAll();
    }
}
