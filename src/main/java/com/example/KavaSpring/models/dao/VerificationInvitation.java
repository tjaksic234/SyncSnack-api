package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "verificationCodes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerificationInvitation {

    @Id
    private String id;

    @Email
    @NotBlank
    private String email;

    @Size(max = 50)
    private String verificationCode;

    @NotBlank
    private boolean active = true;

    @NotBlank
    private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

}
