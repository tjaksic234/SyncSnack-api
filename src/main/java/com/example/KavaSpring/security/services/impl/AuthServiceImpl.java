package com.example.KavaSpring.security.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.GroupMembershipDto;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.models.enums.Role;
import com.example.KavaSpring.repository.*;
import com.example.KavaSpring.security.api.dto.*;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.security.utils.EmailTemplates;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.security.utils.JwtUtils;
import com.example.KavaSpring.services.AmazonS3Service;
import com.example.KavaSpring.services.SendGridEmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    private final PasswordResetRequestRepository passwordResetRequestRepository;

    private final AmazonS3Service amazonS3Service;

    private final GroupMembershipRepository groupMembershipRepository;

    private final GroupRepository groupRepository;

    private final ConverterService converterService;

    @Value("${backend.url.dev}")
    private String BACKEND_URL;

    @Value("${frontend.url.dev}")
    private String FRONTEND_URL;

    @Value("${spring.sendgrid.email-from}")
    private String EMAIL_FROM;

    @Override
    public UserDto fetchMe() {
        log.info("Fetch me started");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDto userDto = new UserDto();

        if (authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            User user = userRepository.findByEmail(userDetails.getEmail())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            UserProfile userProfile = userProfileRepository.getUserProfileById(userDetails.getUserProfileId())
                    .orElseThrow(() -> new IllegalStateException("There was an issue with the retrieved user profile"));


            userDto.setUserProfileId(userProfile.getId());
            userDto.setFirstName(userProfile.getFirstName());
            userDto.setLastName(userProfile.getLastName());
            userDto.setProfileUri(userProfile.getPhotoUri());
            userDto.setVerified(true);
            userDto.setEmail(((UserDetailsImpl) authentication.getPrincipal()).getEmail());

            List<GroupMembership> groupMemberships = groupMembershipRepository.findAllByUserProfileId(userProfile.getId());
            List<GroupMembershipDto> groupMembershipDtoData = new ArrayList<>();

            for (GroupMembership membership : groupMemberships) {
                Group group = groupRepository.findById(membership.getGroupId())
                        .orElseThrow(() -> new RuntimeException("Group not found"));

                GroupMembershipDto groupMembershipDto = new GroupMembershipDto();
                groupMembershipDto.setGroupId(group.getId());
                groupMembershipDto.setActive(membership.isActive());
                groupMembershipDto.setRoles(membership.getRoles());

                groupMembershipDtoData.add(groupMembershipDto);
            }
            userDto.setGroupMembershipData(groupMembershipDtoData);


            log.info("User successfully \"{}\" fetched.", userDto);
            return userDto;
        } else {
            log.error("User not found in security context");
            throw new IllegalStateException("User not found in security context");
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isVerified()) {
            sendVerificationEmail(user);
            throw new UnverifiedUserException("User is not verified. Please check your email for verification instructions.");
        }

        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(user.getId());
        if (userProfile == null) {
            throw new EntityNotFoundException("User profile not found. Please set up your profile.");
        }

        LoginResponse response = new LoginResponse();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);

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
        user.setRoles(List.of(Role.USER));
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

        String verificationUrl = BACKEND_URL + "/api/auth/verify?invitationId=" + invitation.getId()
                + "&verificationCode=" + verificationCode
                + "&userId=" + user.getId();

        URL companyLogoUrl = amazonS3Service.generatePresignedUrl("profilePhotos/syncsnack.png");

        //? sending the email
        sendGridEmailService.sendHtml(EMAIL_FROM, user.getEmail(), "Verification email", EmailTemplates.confirmationEmail(user.getEmail(), verificationUrl, companyLogoUrl));

    }

    @Override
    public void verifyUser(String invitationId, String verificationCode) {
        VerificationInvitation invitation = verificationInvitationRepository.findByIdAndVerificationCode(invitationId, verificationCode);

        if (!invitation.isActive() || invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setActive(false);
            verificationInvitationRepository.save(invitation);
            throw new IllegalStateException("Invitation is no longer valid.");
        }

        Optional<User> user = userRepository.findByEmail(invitation.getEmail());

        if (user.isPresent()) {
            if (!user.get().isVerified()) {
                user.get().setVerified(true);
                invitation.setActive(false);
                userRepository.save(user.get());
                verificationInvitationRepository.save(invitation);
                log.info("User verification successful");
            }
        }

    }

    @Override
    public void changePassword(PasswordChangeRequest request) {
        Optional<User> user = userRepository.findById(Helper.getLoggedInUserId());

        if (user.isPresent() && user.get().isVerified()) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.get().getPassword())) {
                throw new InvalidPasswordException("Wrong old password");
            }
            user.get().setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user.get());
            log.info("Password changed successfully");
        } else {
            throw new NotFoundException("Error occurred when fetching user");
        }
    }

    @Override
    public void requestPasswordReset(PasswordResetRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            throw new NotFoundException("No user with the provided email found");
        }

        String resetCode = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setEmail(request.getEmail());
        passwordResetToken.setResetCode(resetCode);

        passwordResetRequestRepository.save(passwordResetToken);

        String resetPasswordUrl = FRONTEND_URL + "/forgot-password?passwordResetTokenId=" + passwordResetToken.getId()
                + "&resetCode=" + resetCode;

        sendGridEmailService.sendHtml(EMAIL_FROM, user.get().getEmail(), "Reset password", EmailTemplates.resetPassword(resetPasswordUrl));
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetRequestRepository.findById(request.getPasswordResetTokenId());

        if (request.getResetCode().isEmpty()) {
            throw new NotFoundException("No password reset entity found with the provided id");
        }

        if (!passwordResetToken.get().isActive() || passwordResetToken.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetToken.get().setActive(false);
            passwordResetRequestRepository.save(passwordResetToken.get());
            throw new IllegalStateException("The reset password request is no longer valid.");
        }

        if (!request.getResetCode().equals(passwordResetToken.get().getResetCode())) {
            throw new IllegalArgumentException("Invalid reset code");
        }

        Optional<User> user = userRepository.findByEmail(passwordResetToken.get().getEmail());

        if (user.isPresent()) {
            user.get().setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user.get());

            passwordResetToken.get().setActive(false);
            passwordResetRequestRepository.save(passwordResetToken.get());

            log.info("Password reset successfully for user: {}", user.get().getEmail());
        } else {
            throw new NotFoundException("User not found for the given reset password code");
        }

    }

}
