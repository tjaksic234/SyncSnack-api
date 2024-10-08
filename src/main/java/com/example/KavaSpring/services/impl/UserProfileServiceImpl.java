package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnverifiedUserException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dao.GroupMembership;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.repository.GroupMembershipRepository;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.security.utils.StatisticUtils;
import com.example.KavaSpring.services.AmazonS3Service;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserRepository userRepository;

    private final ConverterService converterService;

    private final AmazonS3Service amazonS3Service;

    private final MongoTemplate mongoTemplate;

    private final GroupMembershipRepository groupMembershipRepository;

    private final GroupRepository groupRepository;

    @Override
    public UserProfileResponse createUserProfile(UserProfileRequest request, MultipartFile photoFile) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        boolean exists = userProfileRepository.existsByUserId(request.getUserId());

        if (!user.isVerified()) {
            throw new UnverifiedUserException("User is not verified");
        }

        if (exists) {
            throw new UserProfileExistsException("The User Profile already exists");
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(request.getUserId());
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
    public UserProfileDto getProfileById(String groupId, String id) {
        //? currently a better solution needs to be figured out since the reason for these if blocks is
        //? the api is being used by both the web and mobile application and different logic is being applied for this method
        if (groupId != null && !groupId.isEmpty()) {
            groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group found"));
        }
        UserProfile userProfile = userProfileRepository.getUserProfileById(id)
                .orElseThrow(() -> new NotFoundException("User profile not found"));

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUserId(userProfile.getUserId());
        userProfileDto.setFirstName(userProfile.getFirstName());
        userProfileDto.setLastName(userProfile.getLastName());
        userProfileDto.setPhotoUrl(converterService.convertPhotoUriToUrl(userProfile.getPhotoUri()));

        if (groupId != null && !groupId.isEmpty()) {
            GroupMembership membership = groupMembershipRepository.findByUserProfileIdAndGroupId(id, groupId);
            userProfileDto.setGroupId(membership.getGroupId());
            userProfileDto.setScore(membership.getScore());
        }

        log.info("Get profile by id finished");
        return userProfileDto;
    }

    @Override
    public byte[] downloadUserProfilePhoto() throws IOException {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        if (userProfile == null) {
            throw new NotFoundException("The UserProfile was not retrieved successfully");
        }

        String fileUri = userProfile.getPhotoUri();
        if (fileUri == null || fileUri.trim().isEmpty()) {
            throw new IllegalArgumentException("The photoUri field is missing or empty in the UserProfile.");
        }
        log.info("fileUri: {}", fileUri);
        return amazonS3Service.downloadFromS3(fileUri);
    }

    @Override
    public UserProfileEditResponse editUserProfile(String firstName, String lastName, MultipartFile photoFile) {
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
        UserProfileEditResponse response = new UserProfileEditResponse();
        response.setPhotoUrl(converterService.convertPhotoUriToUrl(userProfile.getPhotoUri()));
        return response;
    }

    @Override
    public void updateProfileScores(String groupId) {
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group associated with the groupId"));

        MatchOperation matchByGroup = Aggregation.match(Criteria.where("groupId").is(groupId));

        AddFieldsOperation convertIdToString = Aggregation.addFields()
                .addField("_id").withValue(ConvertOperators.ToString.toString("$_id"))
                .build();

        LookupOperation lookupOrdersScore = Aggregation.lookup("orders", "_id", "eventId", "orders");

        UnwindOperation unwindOrders = Aggregation.unwind("orders");

        MatchOperation matchRatedOrders = Aggregation.match(Criteria.where("orders.rating").ne(0));

        GroupOperation groupByProfile = Aggregation.group("userProfileId")
                .avg("orders.rating").as("score");

        ProjectionOperation projectFields = Aggregation.project()
                .and("$_id").as("userProfileId")
                .and("score").as("score")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(
                matchByGroup,
                convertIdToString,
                lookupOrdersScore,
                unwindOrders,
                matchRatedOrders,
                groupByProfile,
                projectFields
        );

        AggregationResults<ScoreCalculationDto> results = mongoTemplate.aggregate(aggregation, "events", ScoreCalculationDto.class);

        List<ScoreCalculationDto> profileScores = results.getMappedResults();
        log.info("Results: {}", profileScores);

        for (ScoreCalculationDto scoreDto : profileScores) {
            GroupMembership membership = groupMembershipRepository.findByUserProfileIdAndGroupId(scoreDto.getUserProfileId(), groupId);
            membership.setScore(scoreDto.getScore());
            groupMembershipRepository.save(membership);
        }

        log.info("Updated scores for group: {}. Number of profiles updated: {}", groupId, profileScores.size());
    }


    @Override
    public void updateFcmToken(String token) {
        UserProfile userProfile = userProfileRepository.findByUserId(Helper.getLoggedInUserId())
                .orElseThrow(() -> new IllegalStateException("Bad userProfile retrieved"));
        userProfile.setFcmToken(token);
        userProfileRepository.save(userProfile);
        log.info("FcmToken successfully updated for the user profile");
    }

    @Override
    public List<UserProfileStats> getUserProfileOrderStats(String groupId) {
        List<UserProfileStats> stats = new ArrayList<>();

        Criteria criteria = Criteria.where("userProfileId").is(Helper.getLoggedInUserProfileId());

        if (groupId != null && !groupId.isEmpty()) {
            criteria.and("groupId").is(groupId);
        }

        //* Aggregation for fetching the order count based on the status of the order
        MatchOperation matchByUserProfileId = Aggregation.match(criteria);

        GroupOperation groupByStatus = Aggregation.group("status").count().as("count");

        ProjectionOperation projectOrderStatusCount = Aggregation.project()
                .andExclude("_id")
                .and("_id").as("orderStatus")
                .andInclude("count");

        Aggregation aggregateOrderStatusCount = Aggregation.newAggregation(
                matchByUserProfileId,
                groupByStatus,
                projectOrderStatusCount
        );

        AggregationResults<UserProfileStats> countStatusResults = mongoTemplate.aggregate(aggregateOrderStatusCount, "orders", UserProfileStats.class);

        stats.addAll(countStatusResults.getMappedResults());

        //* Aggregation for fetching the order count based on the event type the order was placed for
        AddFieldsOperation convertStringUserProfileIdToObjectId = Aggregation.addFields().addField("eventId")
                .withValue(ConvertOperators.ToObjectId.toObjectId("$eventId")).build();

        LookupOperation lookupOperation = Aggregation.lookup("events", "eventId", "_id", "events");

        UnwindOperation unwindOperation = Aggregation.unwind("$events");

        GroupOperation groupByEventType = Aggregation.group("events.eventType").count().as("count");

        ProjectionOperation projectOrderTypeCount = Aggregation.project()
                .andExclude("_id")
                .and("_id").as("type")
                .andInclude("count");

        Aggregation aggregateOrderTypeCount = Aggregation.newAggregation(
                matchByUserProfileId,
                convertStringUserProfileIdToObjectId,
                lookupOperation,
                unwindOperation,
                groupByEventType,
                projectOrderTypeCount
        );

        AggregationResults<UserProfileStats> countTypeResults = mongoTemplate.aggregate(aggregateOrderTypeCount, "orders", UserProfileStats.class);

        stats.addAll(countTypeResults.getMappedResults());

        log.info("Fetched the user order stats successfully");
        return stats;
    }

    @Override
    public List<UserProfileStats> getUserProfileEventStats(String groupId) {
        Criteria criteria = Criteria.where("userProfileId").is(Helper.getLoggedInUserProfileId());
        if (groupId != null && !groupId.isEmpty()) {
            criteria.and("groupId").is(groupId);
        }

        MatchOperation matchOperation = Aggregation.match(criteria);
        GroupOperation groupOperation = Aggregation.group("status", "eventType").count().as("count");
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("$_id.status").as("eventStatus")
                .and("$_id.eventType").as("type")
                .andExclude("_id")
                .andInclude("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                projectionOperation
        );

        AggregationResults<UserProfileStats> results = mongoTemplate.aggregate(aggregation, "events", UserProfileStats.class);

        List<UserProfileStats> stats = new ArrayList<>(results.getMappedResults());

        log.info("Fetched the user event stats successfully");
        return stats;
    }

    @Override
    public List<MonthlyStatsDto> fetchMonthlyStats(String groupId, String collection) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime maxTime = currentDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusMinutes(1).withNano(0);
        LocalDateTime minTime = currentDate.minusYears(1).withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1).withNano(0);

        log.info("maxTime: {}", maxTime);
        log.info("minTime: {}", minTime);

        Criteria criteria = new Criteria();
        if (groupId != null && !groupId.isEmpty()) {
            criteria.and("groupId").is(groupId);
        }
        criteria.and("userProfileId").is(Helper.getLoggedInUserProfileId());
        criteria.and("createdAt").gte(minTime).lt(maxTime);

        MatchOperation matchOperation = Aggregation.match(criteria);

        ProjectionOperation project1 = Aggregation.project()
                .and(DateOperators.dateOf("createdAt").year()).as("year")
                .and(DateOperators.dateOf("createdAt").month()).as("month");

        GroupOperation groupOperation = Aggregation.group("year", "month").count().as("count");

        ProjectionOperation project2 = Aggregation.project()
                .and("_id.year").as("year")
                .and("_id.month").as("month")
                .andInclude("count")
                .andExclude("_id");


        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, "year", "month"));

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                project1,
                groupOperation,
                project2,
                sortOperation
        );

        AggregationResults<MonthlyStatsDto> results = mongoTemplate.aggregate(aggregation, collection, MonthlyStatsDto.class);

        log.info("Fetched monthly stats for {}", collection);
        return StatisticUtils.fillMissingMonths(results.getMappedResults(), minTime, maxTime);
    }

}
