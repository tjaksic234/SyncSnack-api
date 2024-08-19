package com.example.KavaSpring.security.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.api.dto.*;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.security.utils.EmailTemplates;
import com.example.KavaSpring.security.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@ShowAPI
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthService authService;

    private final JwtUtils jwtUtils;

    @PostMapping("register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest request) {
      try {
          log.info("Register user started");
          return ResponseEntity.ok(authService.register(request));
      } catch (UserAlreadyExistsException e) {
          log.error(e.getMessage());
          return ResponseEntity.badRequest().build();
      }
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            LoginResponse response = authService.login(request);
            log.info("Success login with: \"{}\"", request.getEmail());
            ResponseCookie cookie = jwtUtils.createJwtCookie(response.getAccessToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (UnauthorizedException e) {
            log.error(String.format("Exception on user authentication: %s", e.getMessage()));
            return ResponseEntity.badRequest().build();
        } catch (UnverifiedUserException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("fetchMe")
    public ResponseEntity<UserDto> fetchMe() {
        try {
            log.info("Fetch me started.");
            return ResponseEntity.ok(authService.fetchMe());
        } catch (Exception e) {
            log.error("Error on fetch me: {}!", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("verify")
    public ResponseEntity<String> verifyUser(
            @RequestParam String invitationId,
            @RequestParam String verificationCode,
            @RequestParam String userId
    ) {
        try {
            log.info("Verify user started");
            authService.verifyUser(invitationId, verificationCode);
            return ResponseEntity.ok(EmailTemplates.emailVerified(userId));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }

    @PostMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            log.info("Changing password for user");
            authService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("reset-password-request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        try {
            log.info("Attempting to send reset password email");
            authService.requestPasswordReset(request);
            return ResponseEntity.ok("Reset password email sent");
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("resetPassword")
    public ResponseEntity<?> resetPassword(
            @RequestParam String passwordResetTokenId,
            @RequestParam String resetCode
    ) {
        try {
            log.info("Resetting password");
            authService.resetPassword(passwordResetTokenId, resetCode);
            return ResponseEntity.ok("Password successfully reset");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
