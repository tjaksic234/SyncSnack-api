package com.example.KavaSpring.exceptions;

public class ExpiredInvitationException extends RuntimeException {
    public ExpiredInvitationException(String message) {
        super(message);
    }
}
