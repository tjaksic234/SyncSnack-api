package com.example.KavaSpring.service.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.service.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        boolean exists = userRepository.existsById(request.getUserId());

        log.info("Creating user profile finished");
        return converterService.convertToUserProfileResponse(request);

    }

    @Override
    public UserProfileDto getProfileById(String id) {
        UserProfile userProfile = userProfileRepository.getUserProfileById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));

        log.info("Get profile by id finished");
        return converterService.convertToUserProfileDto(userProfile);
    }


}
