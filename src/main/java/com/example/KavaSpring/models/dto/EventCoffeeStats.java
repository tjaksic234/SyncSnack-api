package com.example.KavaSpring.models.dto;

import lombok.Data;

@Data
public class EventCoffeeStats {

    private String eventId;
    private float averageRating;
    private int coffeeCount;
}
