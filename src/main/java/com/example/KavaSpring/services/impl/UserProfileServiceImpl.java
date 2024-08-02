package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.config.AmazonS3Config;
import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.GroupMemberResponse;
import com.example.KavaSpring.models.dto.UserProfileDto;
import com.example.KavaSpring.models.dto.UserProfileRequest;
import com.example.KavaSpring.models.dto.UserProfileResponse;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

    private final ConverterService converterService;

    private final AmazonS3Config amazonS3Config;

    private final MongoTemplate mongoTemplate;

    @Override
    public UserProfileResponse createUserProfile(UserProfileRequest request, MultipartFile photoFile) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean exists = userProfileRepository.existsByUserId(request.getUserId());

        if (exists) {
            throw new UserProfileExistsException("The User Profile already exists");
        }

        if (!groupRepository.existsById(request.getGroupId())) {
            log.error("groupId: {}",request.getGroupId());
            throw new NotFoundException("The group with the given id does not exist");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(request.getUserId());
        userProfile.setGroupId(request.getGroupId());
        userProfile.setFirstName(request.getFirstName());
        userProfile.setLastName(request.getLastName());


        //? treba jos optimizirati  putanju i filename bezveze je zakomplicirano
        if (photoFile != null) {
            try {
                String fileName = request.getUserId();
                String path = "profilePhotos";

                amazonS3Config.uploadToS3(path, fileName, photoFile.getInputStream());

                userProfile.setPhotoUri(path +  "/"  + fileName);

                log.info("File uploaded successfully to S3: {}", fileName);
            } catch (IOException e) {
                log.error("Error uploading file to S3", e);
                throw new RuntimeException("Failed to upload file to S3", e);
            }
        }

        userProfileRepository.save(userProfile);

        log.info("User profile created");
        return converterService.convertToUserProfileResponse(request);
    }

    @Override
    public UserProfileDto getProfileById(String id) {
        UserProfile userProfile = userProfileRepository.getUserProfileById(id)
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        log.info("Get profile by id finished");
        return converterService.convertToUserProfileDto(userProfile);
    }

    @Override
    public byte[] downloadUserProfilePhoto() throws IOException {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        if (userProfile == null) {
            throw new NotFoundException("The UserProfile was not retrieved successfully");
        }
        String fileUri = userProfile.getPhotoUri();
        log.info("fileUri: {}", fileUri);
        return amazonS3Config.downloadFromS3(fileUri);
    }

    @Override
    public String editUserProfile(String firstName, String lastName, MultipartFile photoFile) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        if (userProfile == null) {
            throw new IllegalStateException("UserProfile is null");
        }

        if (photoFile != null) {
            try {
                String fileName = userProfile.getId();
                String path = "profilePhotos";

                amazonS3Config.updateFileInS3(path, fileName, photoFile.getInputStream());

                userProfile.setPhotoUri(path +  "/"  + fileName);

                log.info("UserProfile photo updated successfully");
            } catch (IOException e) {
                log.error("Error updating the profile photo", e);
                throw new RuntimeException("Failed to update the UserProfile photo", e);
            }
        }


        if (firstName != null && !firstName.trim().isEmpty()) {
            userProfile.setFirstName(firstName);
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            userProfile.setLastName(lastName);
        }

        userProfileRepository.save(userProfile);

        log.info("UserProfile successfully updated");
        return "UserProfile successfully updated";
    }

    @Override
    public List<GroupMemberResponse> getGroupMembers() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        String groupId = userProfile.getGroupId();
        List<GroupMemberResponse> groupMembers = new ArrayList<>();

        if (groupId.isEmpty()) {
            throw new IllegalStateException("Bad groupId value present in the user profile");
        }

        MatchOperation matchUserProfilesByGroupId = Aggregation.match(Criteria.where("groupId").is(groupId));

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("photoUri").as("profilePhoto")
                .andInclude("firstName")
                .andInclude("lastName")
                .andInclude("groupId")
                .andInclude("score");

        Aggregation userProfileAggregation = Aggregation.newAggregation(
                matchUserProfilesByGroupId,
                projectionOperation
        );

        List<Document> userProfiles = mongoTemplate.aggregate(userProfileAggregation, "userProfiles", Document.class).getMappedResults();

        for (Document userProfileDoc: userProfiles) {
            String userProfileId = userProfileDoc.getObjectId("_id").toString();

            MatchOperation matchOperation = Aggregation.match(Criteria.where("userProfileId").is(userProfileId));
            CountOperation countOperation = Aggregation.count().as("orderCount");

            Aggregation orderCountAggregation = Aggregation.newAggregation(
                    matchOperation,
                    countOperation
            );

            Document result = mongoTemplate.aggregate(orderCountAggregation, "orders", Document.class).getUniqueMappedResult();

            int orderCount = (result != null) ? result.getInteger("orderCount") : 0;

            GroupMemberResponse groupMember  = new GroupMemberResponse();
            //groupMember.setPhotoUri(userProfileDoc.getString("profilePhoto"));
            groupMember.setFirstName(userProfileDoc.getString("firstName"));
            groupMember.setLastName(userProfileDoc.getString("lastName"));
            groupMember.setScore(userProfileDoc.getDouble("score").floatValue());
            groupMember.setOrderCount(orderCount);

            String photoUri = userProfileDoc.getString("profilePhoto");
            if (photoUri != null && !photoUri.isEmpty()) {
                URL presignedUrl = amazonS3Config.generatePresignedUrl(photoUri);
                groupMember.setPhotoUrl(presignedUrl.toString());
            }


            groupMembers.add(groupMember);
        }
        return groupMembers;
    }


}
