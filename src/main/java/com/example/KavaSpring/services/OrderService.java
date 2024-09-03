package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String groupId, OrderRequest request);
    OrderDto getOrderById(String id);
    List<OrderEventInfoDto> getAllOrdersFromUserProfile(String groupId, Pageable pageable, int rating, OrderStatus status, EventType eventType, String search);
    List<OrderActivityResponse> getOrdersByActivityStatus(String groupId, boolean isActive);
    String updateOrderStatus(String id, OrderStatus status);
    List<OrderExpandedResponse> getActiveOrdersByEventId(String id);
    String rateOrder(String id, int rating);
}
