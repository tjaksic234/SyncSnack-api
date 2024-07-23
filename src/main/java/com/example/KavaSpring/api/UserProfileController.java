package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/profiles")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> createUserProfile(@RequestBody UserProfileRequest request) {
        try {
            log.info("Create a profile requested");
            return ResponseEntity.ok(userProfileService.createUserProfile(request));
        } catch (NotFoundException | UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<UserProfileDto> getProfileById(@PathVariable("id") String id) {
        try {
            log.info("Fetching profile by id");
            return ResponseEntity.ok(userProfileService.getProfileById(id));
        } catch (NotFoundException | UnauthorizedException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
