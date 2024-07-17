package com.example.KavaSpring.exceptions;

public class BrewEventAlreadyExistsException extends RuntimeException{
    public BrewEventAlreadyExistsException(String message) {
        super(message);
    }
}
