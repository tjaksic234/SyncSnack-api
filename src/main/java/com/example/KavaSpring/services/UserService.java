package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.UserDto;

import java.util.Map;

public interface UserService {

    UserDto getUserById(String id);
    boolean checkEmail(String email);
    boolean isUserVerified(String email);
}
