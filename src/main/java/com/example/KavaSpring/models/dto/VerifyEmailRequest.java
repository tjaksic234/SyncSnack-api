package com.example.KavaSpring.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyEmailRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String verificationInvitationId;

    @NotBlank
    private String verificationCode;

}
