package com.example.KavaSpring.api.dto;

import com.example.KavaSpring.models.dao.enums.CoffeeType;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateCoffeeOrderRequest {

    @NotBlank
    private String eventId;

    @NotBlank
    private String creatorId;

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
