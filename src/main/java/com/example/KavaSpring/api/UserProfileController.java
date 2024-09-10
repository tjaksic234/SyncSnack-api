package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.exceptions.*;
import com.example.KavaSpring.models.dto.*;
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
    public ResponseEntity<UserProfileDto> getProfileById (
            @RequestHeader("groupId") String groupId,
            @PathVariable("id") String id
    ) {
        try {
            log.info("Fetching profile by id");
            return ResponseEntity.ok(userProfileService.getProfileById(groupId, id));
        } catch (NotFoundException | NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile-photo/download")
    public ResponseEntity<byte[]> getUserProfilePhoto() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(userProfileService.downloadUserProfilePhoto(), headers, HttpStatus.OK);
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

    @PatchMapping("update-scores")
    public ResponseEntity<Void> updateUserProfileScores(@RequestHeader(value = "groupId") String groupId) {
        try {
            log.info("Updating user profile scores started");
            userProfileService.updateProfileScores(groupId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (NoGroupFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("fcm-token")
    public ResponseEntity<?> updateFcmToken(@RequestParam String token) {
        try {
            log.info("Updating fcm token");
            userProfileService.updateFcmToken(token);
            return ResponseEntity.ok("UserProfile fcmToken successfully updated");
        } catch (IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("orders/stats")
    public ResponseEntity<List<UserProfileStats>> getUserProfileOrderStats() {
        try {
            log.info("Fetching order stats for the user");
            return ResponseEntity.ok(userProfileService.getUserProfileOrderStats());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("events/stats")
    public ResponseEntity<List<UserProfileStats>> getUserProfileEventStats() {
        try {
            log.info("Fetching event stats for the user");
            return ResponseEntity.ok(userProfileService.getUserProfileEventStats());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("monthly-summary/{collection}")
    public ResponseEntity<List<MonthlyStatsDto>> getMonthlyOrderStats(@PathVariable String collection) {
        try {
            log.info("Fetching monthly stats");
            return ResponseEntity.ok(userProfileService.fetchMonthlyStats(collection));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
