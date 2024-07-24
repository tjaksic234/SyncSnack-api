package com.example.KavaSpring.exceptions;

public class UserProfileExistsException extends RuntimeException{
    public UserProfileExistsException(String message) {
        super(message);
    }
}
