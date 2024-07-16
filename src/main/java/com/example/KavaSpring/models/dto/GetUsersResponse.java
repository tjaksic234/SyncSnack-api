package com.example.KavaSpring.models.dto;

import lombok.Data;

@Data
public class GetUsersResponse {
    private String email;
    private String firstName;
    private String lastName;
    private int coffeeCounter;
    private int coffeeRating;
}
