package com.example.KavaSpring.security.utils;

import com.example.KavaSpring.models.dto.SimpleIdEmailDto;
import com.example.KavaSpring.security.services.impl.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class Helper {
    public static SimpleIdEmailDto getLoggedSimpleUserIdEmail() {
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

    public static String getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) authentication.getPrincipal()).getId();
        }
        return null;
    }

}