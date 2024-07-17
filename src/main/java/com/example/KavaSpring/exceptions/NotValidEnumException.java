package com.example.KavaSpring.exceptions;

public class NotValidEnumException extends RuntimeException{
    public NotValidEnumException(String message) {
        super(message);
    }
}
