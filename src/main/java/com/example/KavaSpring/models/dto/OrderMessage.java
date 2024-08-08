package com.example.KavaSpring.models.dto;

import lombok.Data;

@Data
public class OrderMessage {
    private String orderId;
    private String eventId;
    private String userProfileId;
}
