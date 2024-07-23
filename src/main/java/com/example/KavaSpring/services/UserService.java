package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.UserDto;

public interface UserService {

    UserDto getUserById(String id);
}
