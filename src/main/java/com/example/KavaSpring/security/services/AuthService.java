package com.example.KavaSpring.security.services;

import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.api.dto.LoginRequest;
import com.example.KavaSpring.security.api.dto.LoginResponse;
import com.example.KavaSpring.security.api.dto.RegisterUserRequest;

public interface AuthService {
    UserDto fetchMe();
    LoginResponse login(LoginRequest request);
    String register(RegisterUserRequest request);
}
