package com.example.KavaSpring.exceptions;

public class UserAlreadyExistsException extends RuntimeException{

    private final String message = "User already exists.";


    public UserAlreadyExistsException() {
        super();
    }
}
