package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderNotification {
    private String orderId;
    private String userProfileId;
    private String firstName;
    private String lastName;
    private String eventId;
    private String groupId;
    private HashMap<String, Object> additionalOptions;
    private LocalDateTime createdAt;
    private String profilePhoto;
}
