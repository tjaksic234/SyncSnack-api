package com.example.KavaSpring.api.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBrewEventRequest {

    @NotBlank
    private String creatorId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime endTime;
}
