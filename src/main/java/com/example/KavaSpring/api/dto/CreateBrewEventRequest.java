package com.example.KavaSpring.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBrewEventRequest {

    @NotBlank
    private String creatorId;

    @NotNull
    private int pendingTime;
}
