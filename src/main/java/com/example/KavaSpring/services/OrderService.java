package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderDto getOrderById(String id);
    List<OrderEventInfoDto> getAllOrdersFromUserProfile();
    List<OrderActivityResponse> getOrdersByActivityStatus(boolean isActive);
    String updateOrderStatus(String id, OrderStatus status);
    String updateAllOrdersStatus(String id, OrderStatus status);
    List<OrderExpandedResponse> getOrdersByEventId(String id);
    String rateOrder(String id, int rating);
}
