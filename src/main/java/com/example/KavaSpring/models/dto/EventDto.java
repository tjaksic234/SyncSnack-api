package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    private String creatorId;
    private String title;
    private String description;
    private String groupId;
    private EventStatus status;
    private EventType eventType;
    private LocalDateTime createdAt;
}
