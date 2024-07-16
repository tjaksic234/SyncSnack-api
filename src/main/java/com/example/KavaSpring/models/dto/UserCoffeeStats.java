package com.example.KavaSpring.models.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCoffeeStats {

    @NotBlank
    private String userId;

    @NotNull
    private double averageRating;
}
