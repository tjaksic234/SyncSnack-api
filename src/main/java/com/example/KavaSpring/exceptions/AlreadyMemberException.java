package com.example.KavaSpring.exceptions;

public class AlreadyMemberException extends RuntimeException{
    public AlreadyMemberException(String message) {
        super(message);
    }
}
