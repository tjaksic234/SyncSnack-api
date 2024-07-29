package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSearchRequest {

    @NotBlank
    @Size(max = 50)
    private String userProfileId;

    @NotBlank
    @Size(max = 50)
    private String groupId;

    @NotBlank
    private EventStatus status;

    @NotBlank
    private EventType eventType;

}
