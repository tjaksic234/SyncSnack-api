package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "groupMemberships")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMembership {
    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String userProfileId;

    @NotBlank
    @Size(max = 50)
    private String groupId;

    @NotNull
    private float score;

    List<Role> roles = new ArrayList<>(List.of(Role.USER));

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
