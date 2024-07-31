package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderExpandedResponse {
    @NotBlank
    @Size(max = 50)
    private String orderId;

    @NotBlank
    @Size(max = 50)
    private String userProfileId;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private HashMap<String, Object> additionalOptions;

    @NotBlank
    private OrderStatus status;

    @NotBlank
    private LocalDateTime createdAt;
}
