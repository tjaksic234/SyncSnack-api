package com.example.KavaSpring.models.dto;

import lombok.Data;

@Data
public class GetUserResponse {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private int coffeeNumber;
    private double score;
}
