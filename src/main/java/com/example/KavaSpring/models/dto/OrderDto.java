package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {
    private String orderedBy;
    private OrderStatus status;
    private HashMap<String, Object> additionalOptions;
    private LocalDateTime createdAt;
}
