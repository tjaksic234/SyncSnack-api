package com.example.KavaSpring.exceptions;

public class OrderAlreadyRatedException extends RuntimeException {
    public OrderAlreadyRatedException(String message) {
        super(message);
    }
}
