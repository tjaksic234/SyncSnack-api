package com.example.KavaSpring.security.services;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.api.dto.LoginRequest;
import com.example.KavaSpring.security.api.dto.LoginResponse;
import com.example.KavaSpring.security.api.dto.RegisterUserRequest;
import com.example.KavaSpring.security.api.dto.RegisterUserResponse;

public interface AuthService {
    UserDto fetchMe();
    LoginResponse login(LoginRequest request);
    RegisterUserResponse register(RegisterUserRequest request);
    void sendVerificationEmail(User user);
    void verifyUser(String invitationId, String verificationCode);
}
