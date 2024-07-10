package com.example.KavaSpring.api.dto;

import com.example.KavaSpring.models.dao.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EditBrewEventRequest {

    @NotBlank
    private String creatorId;

    @NotBlank
    private String eventId;

    @NotNull
    private EventStatus eventStatus;
}
