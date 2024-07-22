package com.example.KavaSpring.models.dto;

import lombok.Data;

@Data
public class UserProfileResponse {

    private String userId;
    private String groupId;
    private String firstName;
    private String lastName;
}
