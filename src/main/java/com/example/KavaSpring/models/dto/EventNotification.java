package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventNotification {
    private String eventId;
    private String groupId;
    private String firstName;
    private String lastName;
    private String title;
    private String description;
    private EventType eventType;
    private LocalDateTime createdAt;
    private LocalDateTime pendingUntil;
    private String userProfileId;
}
