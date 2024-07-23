package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserRepository userRepository;

    private final ConverterService converterService;

    @Override
    public UserProfileResponse createUserProfile(UserProfileRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(request.getUserId());
        userProfile.setGroupId(request.getGroupId());
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());
        userProfileRepository.save(userProfile);

        log.info("User profile created");
        return converterService.convertToUserProfileResponse(request);
    }

    @Override
    public UserProfileDto getProfileById(String id) {
        UserProfile userProfile = userProfileRepository.getUserProfileById(id)
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        log.info("Get profile by id finished");
        return converterService.convertToUserProfileDto(userProfile);
    }


}
