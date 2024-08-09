package com.example.KavaSpring.exceptions;

public class UnverifiedUserException extends RuntimeException {
    public UnverifiedUserException(String message) {
        super(message);
    }
}
