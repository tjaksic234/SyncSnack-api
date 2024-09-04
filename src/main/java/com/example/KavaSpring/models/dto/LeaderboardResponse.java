package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LeaderboardResponse {
    private String userProfileId;
    private String photoUrl;
    private String firstName;
    private String lastName;
    private float score;
    private int orderCount;
}
