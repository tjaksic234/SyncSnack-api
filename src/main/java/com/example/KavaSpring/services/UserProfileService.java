package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {

    UserProfileResponse createUserProfile(UserProfileRequest request, MultipartFile photoFile) throws IOException;
    UserProfileDto getProfileById(String id);
    byte[] downloadUserProfilePhoto() throws IOException;
    UserProfileEditResponse editUserProfile(String firstName, String lastName, MultipartFile photoFile);
    List<GroupMemberResponse> getGroupMembers(SortCondition condition);
    void calculateScore();
}
