package com.example.KavaSpring.api.dto;

import com.example.KavaSpring.models.enums.CoffeeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeOrderDto {

    private String coffeeOrderId;
    private String eventId;
    private String userId;
    private CoffeeType type;
    private int sugarQuantity;
    private int milkQuantity;
    private int rating;
}
