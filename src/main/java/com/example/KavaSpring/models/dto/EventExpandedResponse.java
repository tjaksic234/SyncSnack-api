package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventExpandedResponse {
    @NotBlank
    @Size(max = 50)
    private String eventId;

    @NotBlank
    @Size(max = 120)
    private String userProfileId;

    @NotBlank
    private String userProfileFirstName;

    @NotBlank
    private String userProfileLastName;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    @Size(max = 50)
    private String groupId;

    @NotBlank
    private EventStatus status;

    @NotBlank
    private EventType eventType;

    @NotBlank
    private LocalDateTime createdAt;

    @NotBlank
    private LocalDateTime pendingUntil;
}
