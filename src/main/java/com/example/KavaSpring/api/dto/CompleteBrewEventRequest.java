package com.example.KavaSpring.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteBrewEventRequest {
    @NotBlank
    private String creatorId;

    @NotBlank
    private String eventId;
}
