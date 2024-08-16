package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupOrderCountDto {
    private OrderStatus status;
    private int count;
}
