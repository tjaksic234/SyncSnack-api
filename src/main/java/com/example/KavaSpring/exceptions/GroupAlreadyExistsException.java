package com.example.KavaSpring.exceptions;

public class GroupAlreadyExistsException extends RuntimeException {

    private final String message = "Group already exists.";

    public GroupAlreadyExistsException() {
        super();
    }
}
