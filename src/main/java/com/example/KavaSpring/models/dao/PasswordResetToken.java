package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "passwordResetRequests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    private String id;

    @Email
    @NotBlank
    @Size(max = 50)
    private String email;

    @Size(max = 50)
    private String resetCode;

    @NotBlank
    private boolean isActive = true;

    @CreatedDate
    private LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);
}
