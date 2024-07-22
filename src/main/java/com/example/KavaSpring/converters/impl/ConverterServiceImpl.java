package com.example.KavaSpring.converters.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConverterServiceImpl implements ConverterService {


    @Override
    public UserProfileDto convertToUserProfileDto(UserProfile userProfile) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUserId(userProfile.getUserId());
        userProfileDto.setScore(userProfile.getScore());
        userProfileDto.setGroupId(userProfile.getGroupId());
        return userProfileDto;
    }

    @Override
    public UserProfileResponse convertToUserProfileResponse(UserProfileRequest request) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(request.getUserId());
        response.setGroupId(request.getGroupId());
        return response;
    }


}
