package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderActiveResponse {
    private String eventId;
    private String orderId;
    private String creatorId;
    private String title;
    private String description;
    private String groupId;
    private EventStatus status;
    private EventType eventType;
    private LocalDateTime createdAt;
}
