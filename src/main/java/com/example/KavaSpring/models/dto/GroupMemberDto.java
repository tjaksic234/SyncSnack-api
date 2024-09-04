package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberDto {
    private String userProfileId;
    private String firstName;
    private String lastName;
    private List<Role> roles;
    private String photoUrl;
}
