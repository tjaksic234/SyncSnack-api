package com.example.KavaSpring.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDto {
    private String userId;
    private String firstName;
    private String lastName;
    private float score;
    private String groupId;
    private String photoUri;
}
