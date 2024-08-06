package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.AmazonS3Service;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    private final AmazonS3Service amazonS3Service;

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


        if (photoFile != null) {
            try {
                String fileName = request.getUserId();
                String path = "profilePhotos";

                amazonS3Service.uploadToS3(path, fileName, photoFile.getInputStream());

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
        return amazonS3Service.downloadFromS3(fileUri);
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

                amazonS3Service.updateFileInS3(path, fileName, photoFile.getInputStream());

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
            groupMember.setFirstName(userProfileDoc.getString("firstName"));
            groupMember.setLastName(userProfileDoc.getString("lastName"));
            groupMember.setScore(userProfileDoc.getDouble("score").floatValue());
            groupMember.setOrderCount(orderCount);

            String photoUri = userProfileDoc.getString("profilePhoto");
            if (photoUri != null && !photoUri.isEmpty()) {
                URL presignedUrl = amazonS3Service.generatePresignedUrl(photoUri);
                groupMember.setPhotoUrl(presignedUrl.toString());
            }


            groupMembers.add(groupMember);
        }
        return groupMembers;
    }

    @Override
    public void calculateScore() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        if (userProfile == null) {
            throw new EntityNotFoundException("The userProfile was not found");
        }


        //? average score calculation logic
        MatchOperation matchGroup = Aggregation.match(Criteria.where("groupId").is(userProfile.getGroupId()));

        AddFieldsOperation addStringId = Aggregation.addFields().addField("_id")
                .withValue(ConvertOperators.ToString.toString("$_id")).build();

        LookupOperation lookupOperation = Aggregation.lookup("orders", "_id", "eventId", "orders");

        UnwindOperation unwindOperation = Aggregation.unwind("orders");

        MatchOperation matchRating = Aggregation.match(Criteria.where("orders.rating").ne(0));

        GroupOperation groupAndCalculateScore = Aggregation.group("userProfileId")
                .avg("orders.rating").as("score");

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("userProfileId")
                .and("score").as("score")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(
                matchGroup,
                addStringId,
                lookupOperation,
                unwindOperation,
                matchRating,
                groupAndCalculateScore,
                projectionOperation
        );

        AggregationResults<ScoreCalculationDto> results = mongoTemplate.aggregate(aggregation, "events", ScoreCalculationDto.class);

        List<ScoreCalculationDto> userScores = results.getMappedResults();

        for (ScoreCalculationDto score: userScores) {
            Query query = new Query(Criteria.where("_id").is(score.getUserProfileId()));
            Update update = new Update().set("score", score.getScore());
            mongoTemplate.updateFirst(query, update, UserProfile.class);
        }
        log.info("Scores successfully updated");
    }


}
