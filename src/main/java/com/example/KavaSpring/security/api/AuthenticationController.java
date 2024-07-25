package com.example.KavaSpring.security.api;

import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.exceptions.UserAlreadyExistsException;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.security.api.dto.LoginRequest;
import com.example.KavaSpring.security.api.dto.LoginResponse;
import com.example.KavaSpring.security.api.dto.RegisterUserRequest;
import com.example.KavaSpring.security.services.AuthService;
import com.example.KavaSpring.security.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("api/auth")
public class AuthenticationController {

    private final AuthService authService;

    private final JwtUtils jwtUtils;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody RegisterUserRequest request) {
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
        }

    }

    @GetMapping("fetchMe")
    public ResponseEntity<UserDto> fetchMe() {
        try {
            log.info("Fetch me started.");
            return ResponseEntity.ok(authService.fetchMe());
        } catch (Exception exception) {
            log.error("Error on fetch me: {}!", exception.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


}
