package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import lombok.Data;

@Data
public class BrewEventResult {

    private String eventId;
    private String userId;
    private EventStatus status;
}
