package com.example.KavaSpring.security.services.impl;

import com.example.KavaSpring.exceptions.UserAlreadyExistsException;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dao.VerificationInvitation;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.repository.VerificationInvitationRepository;
import com.example.KavaSpring.security.api.dto.LoginRequest;
import com.example.KavaSpring.security.api.dto.LoginResponse;
import com.example.KavaSpring.security.api.dto.RegisterUserRequest;
import com.example.KavaSpring.security.api.dto.RegisterUserResponse;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.security.utils.EmailTemplates;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.security.utils.JwtUtils;
import com.example.KavaSpring.services.SendGridEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final VerificationInvitationRepository verificationInvitationRepository;

    private final SendGridEmailService sendGridEmailService;

    @Value("${FRONTEND_URL}")
    private String FRONTEND_URL;

    @Value("${EMAIL_FROM}")
    private String EMAIL_FROM;

    @Override
    public UserDto fetchMe() {
        log.info("Fetch me started");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = new UserDto();

        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
             UserProfile userProfile = userProfileRepository.getUserProfileByUserId(((UserDetailsImpl) authentication.getPrincipal()).getId());
            if (userProfile != null) {
                userDto.setUserProfileId(userProfile.getId());
                userDto.setFirstName(userProfile.getFirstName());
                userDto.setLastName(userProfile.getLastName());
                userDto.setProfileUri(userProfile.getPhotoUri());
                userDto.setGroupId(userProfile.getGroupId());
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
    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || request.getEmail() == null) {
            throw new UserAlreadyExistsException("Email is already taken or it is not entered in correct format!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        //? Send verification email
        sendVerificationEmail(user);

        RegisterUserResponse response = new RegisterUserResponse();
        response.setUserId(user.getId());

        return response;
    }

    @Override
    public void sendVerificationEmail(User user) {

        String verificationCode = Helper.generateRandomString();
        VerificationInvitation invitation = new VerificationInvitation();
        invitation.setVerificationCode(verificationCode);
        invitation.setEmail(user.getEmail());

        verificationInvitationRepository.save(invitation);

        String verificationUrl = FRONTEND_URL + "/verify/" + invitation.getId() + "/" + verificationCode;

        //? sending the email
        sendGridEmailService.sendHtml(EMAIL_FROM, user.getEmail(), "Verification email", EmailTemplates.confirmationEmail(user.getEmail(), verificationUrl));

    }

}
