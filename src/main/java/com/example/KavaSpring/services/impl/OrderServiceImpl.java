package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dto.OrderDto;
import com.example.KavaSpring.models.dto.OrderRequest;
import com.example.KavaSpring.models.dto.OrderResponse;
import com.example.KavaSpring.repository.OrderRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ConverterService converterService;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        boolean exists = userRepository.existsById(request.getOrderedBy());

        if (!exists) {
            throw new NotFoundException("No user associated with id");
        }

        log.info("the order request is: {}", request);
        Order order = new Order();
        order.setOrderedBy(request.getOrderedBy());
        order.setAdditionalOptions(request.getAdditionalOptions());
        orderRepository.save(order);

        log.info("Order created");
        return converterService.convertToOrderResponse(request);
    }

    @Override
    public OrderDto getOrderById(String id) {
        Order order = orderRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("No order associated with id"));

        log.info("Get order by id finished");
        return converterService.convertToOrderDto(order);
    }
}
