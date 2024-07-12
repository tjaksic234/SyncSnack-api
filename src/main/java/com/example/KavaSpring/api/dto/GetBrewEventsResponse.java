package com.example.KavaSpring.api.dto;

import com.example.KavaSpring.models.dao.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetBrewEventsResponse {

    @NotBlank
    private String eventId;

    @NotBlank
    private String creatorId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private EventStatus status;
}
