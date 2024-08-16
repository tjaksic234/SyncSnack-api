package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderDto getOrderById(String id);
    List<OrderEventInfoDto> getAllOrdersFromUserProfile(Pageable pageable, int rating, OrderStatus status, String search);
    List<OrderActivityResponse> getOrdersByActivityStatus(boolean isActive);
    String updateOrderStatus(String id, OrderStatus status);
    List<OrderExpandedResponse> getActiveOrdersByEventId(String id);
    String rateOrder(String id, int rating);
    List<OrderSearchResponse> searchOrders(OrderSearchRequest request);
}
