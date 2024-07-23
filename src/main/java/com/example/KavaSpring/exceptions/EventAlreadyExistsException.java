package com.example.KavaSpring.exceptions;

public class EventAlreadyExistsException extends RuntimeException{

    private static final String message = "An event which is already PENDING or InPROGRESS exists.";

    public EventAlreadyExistsException() {
        super(message);
    }
}
