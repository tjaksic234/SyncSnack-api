package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.EntityNotFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.exceptions.UnverifiedUserException;
import com.example.KavaSpring.exceptions.UserProfileExistsException;
import com.example.KavaSpring.models.dao.User;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.repository.UserRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.security.utils.StatisticUtils;
import com.example.KavaSpring.services.AmazonS3Service;
import com.example.KavaSpring.services.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    private final GroupRepository groupRepository;

    private final ConverterService converterService;

    private final AmazonS3Service amazonS3Service;

    private final MongoTemplate mongoTemplate;


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
    public List<GroupMemberResponse> getGroupMembers(SortCondition condition, Pageable pageable) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        String groupId = userProfile.getGroupId();
        List<GroupMemberResponse> groupMembers = new ArrayList<>();

        if (groupId.isEmpty()) {
            throw new IllegalStateException("Bad groupId value present in the user profile");
        }

        MatchOperation matchUserProfilesByGroupId = Aggregation.match(Criteria.where("groupId").is(groupId));

        AddFieldsOperation convertToStringId = Aggregation.addFields().addField("_id")
                .withValue(ConvertOperators.ToString.toString("$_id")).build();

        LookupOperation lookupOperation = Aggregation.lookup("orders", "_id", "userProfileId", "orderDetails");

        UnwindOperation unwindOperation = Aggregation.unwind("orderDetails", true);

        GroupOperation groupOperation =  Aggregation.group("_id")
                .first("_id").as("userProfileId")
                .first("firstName").as("firstName")
                .first("lastName").as("lastName")
                .first("score").as("score")
                .first("photoUri").as("photoUri")
                .count().as("orderCount");


        ProjectionOperation projectionOperation = Aggregation.project()
                .and("photoUri").as("photoUrl")
                .andInclude("userProfileId")
                .andInclude("firstName")
                .andInclude("lastName")
                .andInclude("groupId")
                .andInclude("score")
                .andInclude("orderCount");

        SortOperation sortOperation = switch (condition) {
            case SCORE -> Aggregation.sort(Sort.by(Sort.Direction.DESC, "score"));
            case ORDER_COUNT -> Aggregation.sort(Sort.by(Sort.Direction.DESC, "orderCount"));
            case FIRSTNAME -> Aggregation.sort(Sort.by(Sort.Direction.ASC, "firstName"));
        };

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        SkipOperation skipOperation = Aggregation.skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        Aggregation userProfileAggregation = Aggregation.newAggregation(
                matchUserProfilesByGroupId,
                convertToStringId,
                lookupOperation,
                unwindOperation,
                groupOperation,
                projectionOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        List<UserProfileExpandedResponse> userProfiles = mongoTemplate.aggregate(userProfileAggregation, "userProfiles", UserProfileExpandedResponse.class).getMappedResults();

        for (UserProfileExpandedResponse userProfileResponse : userProfiles) {
            GroupMemberResponse groupMember = converterService.convertToGroupMemberResponse(userProfileResponse);
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

    @Override
    public void updateFcmToken(String token) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        if (userProfile == null) {
            throw new NotFoundException("No user profile found with the provided user id");
        }

        userProfile.setFcmToken(token);
        userProfileRepository.save(userProfile);
        log.info("FcmToken successfully updated for the user profile");
    }

    @Override
    public List<UserProfileStats> getUserProfileStats() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        List<UserProfileStats> stats = new ArrayList<>();

        //* Aggregation for fetching the order count based on the status of the order
        MatchOperation matchByUserProfileId = Aggregation.match(Criteria.where("userProfileId").is(userProfile.getId()));

        GroupOperation groupByStatus = Aggregation.group("status").count().as("count");

        ProjectionOperation projectOrderStatusCount = Aggregation.project()
                .andExclude("_id")
                .and("_id").as("status")
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

        log.info("Fetched the user stats successfully");
        return stats;
    }

    @Override
    public List<MonthlyStatsDto> fetchMonthlyStats(String collection) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        String userProfileId = userProfile.getId();

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime maxTime = currentDate.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusMinutes(1).withNano(0);
        LocalDateTime minTime = currentDate.minusYears(1).withHour(0).withMinute(0).withSecond(0).withDayOfMonth(1).withNano(0);

        log.info("maxTime: {}", maxTime);
        log.info("minTime: {}", minTime);

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("userProfileId").is(userProfileId),
                Criteria.where("createdAt").gte(minTime).lt(maxTime)
        );

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
