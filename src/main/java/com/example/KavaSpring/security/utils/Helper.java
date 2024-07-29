package com.example.KavaSpring.security.utils;

import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.SimpleIdEmailDto;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.services.impl.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Helper {

    private final UserProfileRepository userProfileRepository;

    public SimpleIdEmailDto getLoggedSimpleUserIdEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            SimpleIdEmailDto simpleIdNameDto = new SimpleIdEmailDto();
            simpleIdNameDto.setUserId(userDetails.getId());
            simpleIdNameDto.setEmail(userDetails.getEmail());
            return simpleIdNameDto;
        }
        log.error("Failed to get logged-in user details: Authentication is null or principal is not an instance of UserDetailsImpl.");
        return null;
    }

    public String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        return null;
    }

    public String getLoggedInUserProfileId() {
        String userId = getLoggedInUserId();
        if (userId != null) {
            UserProfile userProfile = userProfileRepository.getUserProfileByUserId(userId);
            if (userProfile != null) {
                return userProfile.getId();
            } else {
                log.error("No UserProfile found for userId: {}", userId);
            }
        } else {
            log.error("Failed to get logged-in userId.");
        }
        return null;
    }

    public String getLoggedInUserGroupId() {
        String userId = getLoggedInUserId();
        if (userId != null) {
            UserProfile userProfile = userProfileRepository.getUserProfileByUserId(userId);
            if (userProfile != null) {
                return userProfile.getGroupId();
            } else {
                log.error("No UserProfile found for userId: {}", userId);
            }
        } else {
            log.error("Failed to get logged-in userId.");
        }
        return null;
    }
}
