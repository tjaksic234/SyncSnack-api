package com.example.KavaSpring.api.dto;

import com.example.KavaSpring.models.dao.CoffeeType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCoffeeOrderRequest {

    @NotBlank
    private CoffeeType type;

    @NotNull
    @Min(0)
    @Max(5)
    private int sugarQuantity;

    @NotNull
    @Min(0)
    @Max(5)
    private int milkQuantity;
}
