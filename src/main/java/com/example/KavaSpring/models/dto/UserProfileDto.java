package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String groupId;
    private float score;
    private String photoUrl;
}
