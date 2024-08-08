package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.dao.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotification {
    private Order order;
}
