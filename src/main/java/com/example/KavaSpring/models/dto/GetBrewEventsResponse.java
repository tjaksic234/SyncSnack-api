package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetBrewEventsResponse {

    @NotBlank
    private String eventId;

    @NotBlank
    private String userId;

    @NotBlank
    private EventStatus status;
}
