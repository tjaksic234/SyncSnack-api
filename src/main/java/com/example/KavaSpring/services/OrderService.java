package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.OrderDto;
import com.example.KavaSpring.models.dto.OrderRequest;
import com.example.KavaSpring.models.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderDto getOrderById(String id);
}
