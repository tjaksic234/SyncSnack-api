package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String userProfileId;
    private String email;
    private String firstName;
    private String lastName;
    private String groupId;
    private String profileUri;
    private boolean isVerified;
    private List<Role> roles;
}
