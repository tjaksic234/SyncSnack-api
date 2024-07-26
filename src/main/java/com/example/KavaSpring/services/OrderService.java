package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderDto getOrderById(String id);
    List<OrderActiveResponse> activeOrders(OrderActiveRequest request);
}
