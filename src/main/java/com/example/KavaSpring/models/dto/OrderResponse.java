package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String userProfileId;
    private String eventId;
    private HashMap<String, Object> additionalOptions;
}
