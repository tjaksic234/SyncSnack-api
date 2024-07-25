package com.example.KavaSpring.security.services.impl;

import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public UserDto fetchMe() {
        log.info("Fetch me started");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = new UserDto();

        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            userDto.setEmail(((UserDetailsImpl) authentication.getPrincipal()).getEmail());

            log.info("User successfully \"{}\" fetched.", userDto.getEmail());

            return userDto;
        }
        log.info("User not found.");
        throw new RuntimeException("User not found.");
    }
}
