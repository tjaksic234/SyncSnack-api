package com.example.KavaSpring.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditOrderRequest {

    @NotBlank
    private String coffeeOrderId;

    @NotNull
    private int ratingUpdate;
}
