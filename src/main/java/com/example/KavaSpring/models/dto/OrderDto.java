package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String orderedBy;
    private OrderStatus status;
    private HashMap<String, Object> additionalOptions;
    private LocalDateTime createdAt;
}
