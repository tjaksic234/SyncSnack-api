package com.example.KavaSpring.helper.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import lombok.Data;

@Data
public class BrewEventResult {

    private String eventId;
    private String firstName;
    private String lastName;
    private EventStatus status;
}
