package com.example.KavaSpring.security.services;

import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dto.UserDto;

public interface AuthService {
    UserDto fetchMe();
}
