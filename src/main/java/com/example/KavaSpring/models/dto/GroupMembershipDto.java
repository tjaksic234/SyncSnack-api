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
public class GroupMembershipDto {
    private String groupId;
    private List<Role> roles;
}
