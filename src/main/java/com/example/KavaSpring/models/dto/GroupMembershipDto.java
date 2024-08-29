package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembershipDto {
    private String groupId;
    private boolean active;
    private List<Role> roles;
}
