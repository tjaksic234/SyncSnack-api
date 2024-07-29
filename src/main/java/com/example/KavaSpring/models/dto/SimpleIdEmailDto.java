package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleIdEmailDto {
    private String userId;
    private String email;
}
