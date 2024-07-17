package com.example.KavaSpring.exceptions;

public class EmptyContentException extends RuntimeException{
    public EmptyContentException(String message) {
        super(message);
    }
}
