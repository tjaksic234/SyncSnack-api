package com.example.KavaSpring.security.services.impl;

import com.example.KavaSpring.exceptions.UserAlreadyExistsException;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.security.api.dto.LoginRequest;
import com.example.KavaSpring.security.api.dto.LoginResponse;
import com.example.KavaSpring.security.api.dto.RegisterUserRequest;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.security.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserDto fetchMe() {
        log.info("Fetch me started");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = new UserDto();

        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
             UserProfile userProfile = userProfileRepository.getUserProfileByUserId(((UserDetailsImpl) authentication.getPrincipal()).getId());
            if (userProfile != null) {
                userDto.setFirstName(userProfile.getFirstName());
                userDto.setLastName(userProfile.getLastName());
                userDto.setProfileUri(userProfile.getPhotoUri());
            } else  {
                log.error("The user does not have a setup profile");
            }
            log.info("The user id -----> {}", ((UserDetailsImpl) authentication.getPrincipal()).getId());
            userDto.setEmail(((UserDetailsImpl) authentication.getPrincipal()).getEmail());

            log.info("User successfully \"{}\" fetched.", userDto);

            return userDto;
        }
        log.info("User not found.");
        throw new RuntimeException("User not found.");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        LoginResponse response = new LoginResponse();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);

        ResponseCookie cookie = jwtUtils.createJwtCookie(token);

        response.setAccessToken(token);

        return response;
    }

    @Override
    public String register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || request.getEmail() == null) {
            throw new UserAlreadyExistsException("Email is already taken or it is not entered in correct format!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return "User registered successfully";
    }
}
