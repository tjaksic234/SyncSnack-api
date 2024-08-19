package com.example.KavaSpring.security.services;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.api.dto.*;

public interface AuthService {
    UserDto fetchMe();
    LoginResponse login(LoginRequest request);
    RegisterUserResponse register(RegisterUserRequest request);
    void sendVerificationEmail(User user);
    void verifyUser(String invitationId, String verificationCode);
    void changePassword(PasswordChangeRequest request);
}
