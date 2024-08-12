package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dto.UserDto;
import com.example.KavaSpring.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/users")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class UserController {

    private final UserService userService;


    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") String id) {
        try {
            log.info("Fetching user by id");
            return ResponseEntity.ok(userService.getUserById(id));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("check")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {

        try {
            log.info("Checking email: {}", email);
            return ResponseEntity.ok(userService.checkEmail(email));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("verify")
    public ResponseEntity<Map<String, Boolean>> isUserVerified(@RequestParam String email) {
        try {
            log.info("Checking if user is verified: {}", email);
            boolean isVerified = userService.isUserVerified(email);
            Map<String, Boolean> response = Map.of("isVerified", isVerified);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("id")
    public ResponseEntity<Map<String, String>> getUserIdByEmail(@RequestParam String email) {
        try {
            log.info("Fetching userId by email");
            String userId = userService.getUserIdByEmail(email);
            Map<String, String> response = Map.of("userId", userId);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("profile")
    public ResponseEntity<Map<String, Boolean>> isUserProfilePresent(@RequestParam String userId) {
        try {
            log.info("Checking if the userProfile is present for the userId");
            boolean isPresent = userService.isUserProfilePresent(userId);
            Map<String, Boolean> response = Map.of("isProfilePresent", isPresent);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
        }
    }



}
