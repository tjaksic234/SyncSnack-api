package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.UserDto;

public interface UserService {

    UserDto getUserById(String id);
    boolean checkEmail(String email);
    boolean isUserVerified(String email);
    String getUserIdByEmail(String email);
    boolean isUserProfilePresent(String userId);
}
