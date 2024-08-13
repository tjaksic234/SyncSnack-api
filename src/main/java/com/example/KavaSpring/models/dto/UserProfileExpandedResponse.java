package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileExpandedResponse {
    private String userProfileId;
    private String firstName;
    private String lastName;
    private String groupId;
    private float score;
    private int orderCount;
    private String photoUrl;
}
