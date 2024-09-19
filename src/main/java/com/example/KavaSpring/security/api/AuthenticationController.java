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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@ShowAPI
@RequestMapping("api/auth")
public class AuthenticationController {

    @Value("${frontend.url.dev}")
    private String FRONTEND_URL;

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
            log.info("Log in started");
            LoginResponse response = authService.login(request);
            ResponseCookie cookie = jwtUtils.createJwtCookie(response.getAccessToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (UnauthorizedException | BadCredentialsException e) {
            log.error(String.format("Exception on user authentication: %s", e.getMessage()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (UnverifiedUserException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NotFoundException e) {
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
            return ResponseEntity.ok(EmailTemplates.emailVerified(userId, FRONTEND_URL));
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
        } catch (InvalidPasswordException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
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

    @PostMapping("resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            log.info("Resetting password");
            authService.resetPassword(passwordResetRequest);
            return ResponseEntity.ok("Password successfully reset");
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
