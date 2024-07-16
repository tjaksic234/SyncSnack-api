package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.CoffeeType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCoffeeOrderRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String userId;

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

    private int rating;
}
