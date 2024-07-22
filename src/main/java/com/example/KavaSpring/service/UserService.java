package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dto.UserDto;

public interface UserService {

    UserDto getUserById(String id);
}
