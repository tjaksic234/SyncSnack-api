package com.example.KavaSpring.models.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {

    private String userId;
    private String groupId;
}
