package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter @Setter
public class User {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Min(0)
    @Max(5)
    private int coffeeNumber;

    @Min(0)
    @Max(5)
    private float score;

    public User() {
    }

    public User(String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    public User(String id, String email, String firstName, String lastName, int coffeeNumber, float score) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coffeeNumber = coffeeNumber;
        this.score = score;
    }
}
