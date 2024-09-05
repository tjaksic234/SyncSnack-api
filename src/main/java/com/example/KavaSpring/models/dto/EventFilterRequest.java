package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.TimeFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterRequest {
    private EventStatus status;
    private EventType eventType;
    private TimeFilter timeFilter;
}
