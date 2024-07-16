package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.CoffeeType;
import lombok.Data;

@Data
public class GetOrderResponse {
    private String coffeeOrderId;
    private CoffeeType type;
    private int sugarQuantity;
    private int milkQuantity;
}
