package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;

public interface ConverterService {

    UserProfileDto convertToUserProfileDto(UserProfile userProfile);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
}
