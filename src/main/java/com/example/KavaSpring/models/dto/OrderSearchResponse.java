package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchResponse {
    private String orderId;
    private OrderStatus status;
    private String eventId;
    private EventType eventType;
    private HashMap<String, Object> additionalOptions;
    private int rating;
    private LocalDateTime createdAt;
}
