package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {
    UserProfileResponse createUserProfile(UserProfileRequest request, MultipartFile photoFile) throws IOException;
    UserProfileDto getProfileById(String groupId, String id);
    byte[] downloadUserProfilePhoto() throws IOException;
    UserProfileEditResponse editUserProfile(String firstName, String lastName, MultipartFile photoFile);
    void updateProfileScores(String groupId);
    void updateFcmToken(String token);
    List<UserProfileStats> getUserProfileOrderStats();
    List<UserProfileStats> getUserProfileEventStats();
    List<MonthlyStatsDto> fetchMonthlyStats(String collection);
}
