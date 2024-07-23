package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;

public interface UserProfileService {

    UserProfileResponse createUserProfile(UserProfileRequest request);
    UserProfileDto getProfileById(String id);
}
