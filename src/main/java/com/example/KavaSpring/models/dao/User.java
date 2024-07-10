package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull
    private int coffeeCounter;

    private int coffeeRating;

    @NotBlank
    @Size(max = 120)
    private String password;


    public User() {
    }

    public User(String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.coffeeCounter = 0;
        this.coffeeRating = 0;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", coffeeCounter=" + coffeeCounter +
                ", coffeeRating=" + coffeeRating +
                ", password='" + password + '\'' +
                '}';
    }
}
