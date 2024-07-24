package com.example.KavaSpring.exceptions;

public class EventAlreadyExistsException extends RuntimeException{

    public EventAlreadyExistsException(String message) {
        super(message);
    }
}
