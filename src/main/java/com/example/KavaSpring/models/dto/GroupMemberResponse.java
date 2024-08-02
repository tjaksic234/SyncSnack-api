package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    private String photoUri;
    private String firstName;
    private String lastName;
    private float score;
    private int orderCount;
}
