package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "groupInvitations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupInvitation {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String groupId;

    @NotBlank
    @Size(max = 50)
    private String userProfileId;

    @NotBlank
    @Size(max = 50)
    private String code;

    @NotBlank
    private boolean active = true;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @NotBlank
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

    @NotBlank
    private String invitedBy;
}
