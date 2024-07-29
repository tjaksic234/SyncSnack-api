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
        SimpleIdEmailDto dto = getLoggedSimpleUserIdEmail();
        if (dto != null && dto.getUserId() != null) {
            try {
                return dto.getUserId();
            } catch (Exception e) {
                log.error("Failed to get user ID: {}", dto.getUserId(), e);
            }
        }
        return null;
    }
}
