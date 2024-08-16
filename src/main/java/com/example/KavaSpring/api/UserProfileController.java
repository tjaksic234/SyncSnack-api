package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnverifiedUserException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
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
import java.util.List;

@RestController
@RequestMapping("api/profiles")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @Valid @RequestPart(value = "body") UserProfileRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            log.info("Create a profile requested");
            return ResponseEntity.ok(userProfileService.createUserProfile(request, file));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (UserProfileExistsException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UnverifiedUserException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<UserProfileDto> getProfileById(@PathVariable("id") String id) {
        try {
            log.info("Fetching profile by id");
            return ResponseEntity.ok(userProfileService.getProfileById(id));
        } catch (NotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile-photo/download")
    public ResponseEntity<byte[]> getUserProfilePhoto() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity(userProfileService.downloadUserProfilePhoto(), headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping(value = "edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileEditResponse> editUserProfile(
            @RequestPart(value = "firstName", required = false) String firstName,
            @RequestPart(value = "lastName", required = false) String lastName,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            log.info("UserProfile update edit started");
            return ResponseEntity.ok(userProfileService.editUserProfile(firstName, lastName, file));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("group")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(@RequestParam SortCondition sortCondition) {
        try {
            log.info("Fetching group members");
            return ResponseEntity.ok(userProfileService.getGroupMembers(sortCondition));
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("scores")
    public ResponseEntity<Void> updateUserProfileScores() {
        try {
            log.info("Updating user profile scores started");
            userProfileService.calculateScore();
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
