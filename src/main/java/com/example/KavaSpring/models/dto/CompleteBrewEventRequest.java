package com.example.KavaSpring.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteBrewEventRequest {
    @NotBlank
    private String userId;
}
