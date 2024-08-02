package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.GroupMemberResponse;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {

    UserProfileResponse createUserProfile(UserProfileRequest request, MultipartFile photoFile) throws IOException;
    UserProfileDto getProfileById(String id);
    byte[] downloadUserProfilePhoto() throws IOException;
    String editUserProfile(String firstName, String lastName, MultipartFile photoFile);
    List<GroupMemberResponse> getGroupMembers();
    void calculateScore();
}
