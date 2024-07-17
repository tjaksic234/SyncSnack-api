package com.example.KavaSpring.service;


import com.example.KavaSpring.models.dao.CoffeeOrder;
import com.example.KavaSpring.models.dto.CreateCoffeeOrderRequest;
import com.example.KavaSpring.models.dto.EditOrderRequest;
import com.example.KavaSpring.models.dto.GetOrderResponse;

import java.util.List;

public interface CoffeeOrderService {
    String create(CreateCoffeeOrderRequest request);
    String editOrder(EditOrderRequest request);
    GetOrderResponse getOrderById(String id);
    List<CoffeeOrder> getCoffeeOrders();
}
