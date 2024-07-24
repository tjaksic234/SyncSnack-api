package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnauthorizedException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import com.example.KavaSpring.services.UserProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/profiles")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @Valid @RequestPart(value = "body") UserProfileRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Create a profile requested");
            return ResponseEntity.ok(userProfileService.createUserProfile(request, file));
        } catch (NotFoundException | UnauthorizedException | IOException | UserProfileExistsException e) {
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

    @GetMapping("/profile-photo/{id}")
    public ResponseEntity<byte[]> getUserProfilePhoto(@PathVariable String id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity(userProfileService.downloadUserProfilePhoto(id), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
