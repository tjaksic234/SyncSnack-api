package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.GroupAlreadyExistsException;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dao.GroupMembership;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import com.example.KavaSpring.repository.GroupMembershipRepository;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.AmazonS3Service;
import com.example.KavaSpring.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    private final UserProfileRepository userProfileRepository;

    private final ConverterService converterService;

    private final PasswordEncoder passwordEncoder;

    private final MongoTemplate mongoTemplate;

    private final GroupMembershipRepository groupMembershipRepository;

    private final AmazonS3Service amazonS3Service;


    @Override
    public GroupResponse createGroup(GroupRequest request) {
         Optional<Group> groupOptional = groupRepository.findByName(request.getName());

         if(groupOptional.isPresent()) {
             String existingGroupName = groupOptional.get().getName().toLowerCase();
             String groupNameRequest = request.getName().toLowerCase();
             if (groupNameRequest.equals(existingGroupName)) {
                 throw new GroupAlreadyExistsException("Group name already exists: " + request.getName());
             }
         }
         Group group = new Group();
         group.setName(request.getName());
         group.setDescription(request.getDescription());
         group.setPassword(passwordEncoder.encode(request.getPassword()));
         groupRepository.save(group);

        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getId());
        response.setName(request.getName());
        response.setDescription(request.getDescription());

         log.info("Group created");
         return response;
    }

    @Override
    public GroupDto getGroupById(String id) {
        Group group = groupRepository.getById(id).orElseThrow(() -> new NotFoundException("No group associated with that id"));

        log.info("Get group by id finished");
        return converterService.convertToGroupDto(group);
    }

    @Override
    public GroupResponse joinGroup(GroupRequest request) {
        Group group = groupRepository.findByName(request.getName())
                .orElseThrow(() -> new NotFoundException("Group not found"));

        String userId = Helper.getLoggedInUserId() == null ? request.getUserId() : null;

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("No user profile found when joining a group"));

        if (!passwordEncoder.matches(request.getPassword(), group.getPassword())) {
            throw new IllegalStateException("Invalid password");
        }

        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getId());
        response.setName(request.getName());

        GroupMembership existingMembership = groupMembershipRepository.findByUserProfileIdAndGroupId(userProfile.getId(), group.getId());
        if (existingMembership != null) {
            throw new IllegalStateException("User is already a member of this group");
        }

        //? Saving the relation between a group and the profile
        GroupMembership groupMembership = new GroupMembership();
        groupMembership.setUserProfileId(userProfile.getId());
        groupMembership.setGroupId(group.getId());

        groupMembershipRepository.save(groupMembership);

        log.info("Group join successful");
        return response;
    }

    @Override
    public List<LeaderboardResponse> getLeaderboard(String groupId, SortCondition condition, Pageable pageable) {
        Criteria criteria = Criteria.where("groupId").is(groupId);
        criteria.and("score").gt(0);

        MatchOperation matchByGroup = Aggregation.match(criteria);

        AddFieldsOperation convertToObjectId = Aggregation.addFields()
                .addField("userProfileId")
                .withValue(ConvertOperators.ToObjectId.toObjectId("$userProfileId"))
                .build();

        LookupOperation lookupUserProfiles = Aggregation.lookup("userProfiles", "userProfileId", "_id", "userProfile");

        UnwindOperation unwindUserProfile = Aggregation.unwind("userProfile");

        AddFieldsOperation convertToString = Aggregation.addFields()
                .addField("userProfileId")
                .withValue(ConvertOperators.ToString.toString("$userProfileId"))
                .build();

        LookupOperation lookupOrders = Aggregation.lookup("orders", "userProfileId", "userProfileId", "orderDetails");

        UnwindOperation unwindOrderDetails = Aggregation.unwind("orderDetails", true);

        GroupOperation groupOperation = Aggregation.group("userProfileId")
                .first("userProfile._id").as("userProfileId")
                .first("userProfile.firstName").as("firstName")
                .first("userProfile.lastName").as("lastName")
                .first("userProfile.photoUri").as("photoUrl")
                .first("score").as("score")
                .count().as("orderCount");

        SortOperation sortOperation = switch (condition) {
            case SCORE -> Aggregation.sort(Sort.by(Sort.Direction.DESC, "score"));
            case ORDER_COUNT -> Aggregation.sort(Sort.by(Sort.Direction.DESC, "orderCount"));
            case FIRSTNAME -> Aggregation.sort(Sort.by(Sort.Direction.ASC, "firstName"));
        };

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        SkipOperation skipOperation = Aggregation.skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        Aggregation aggregation = Aggregation.newAggregation(
                matchByGroup,
                convertToObjectId,
                lookupUserProfiles,
                unwindUserProfile,
                convertToString,
                lookupOrders,
                unwindOrderDetails,
                groupOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<UserProfileExpandedResponse> results = mongoTemplate.aggregate(
                aggregation, "groupMemberships", UserProfileExpandedResponse.class
        );

        return results.getMappedResults().stream()
                .map(converterService::convertToLeaderboardResponse)
                .toList();
    }

    @Override
    public List<GroupOrderCountDto> countGroupOrders(String groupId) {

        MatchOperation matchByGroupId = Aggregation.match(Criteria.where("groupId").is(groupId));

        GroupOperation groupByOrderStatus = Aggregation.group("status").count().as("value");

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("name")
                .andInclude("value")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(
                matchByGroupId,
                groupByOrderStatus,
                projectionOperation
        );

        AggregationResults<GroupOrderCountDto> results = mongoTemplate.aggregate(aggregation, "orders", GroupOrderCountDto.class);

        log.info("Fetched the group order count successfully");
        return results.getMappedResults();
    }

    @Override
    public GroupEditResponse editGroupInfo(String groupId, String name, String description, MultipartFile photoFile) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group found"));

        if (photoFile != null) {
            try {
                String fileName = groupId;
                String path = "groupPhotos";

                amazonS3Service.updateFileInS3(path, fileName, photoFile.getInputStream());

                group.setPhotoUri(path + "/" + fileName);

                log.info("Group photo updated successfully");
            } catch (IOException e) {
                log.error("Error updating the group photo", e);
                throw new RuntimeException("Failed to update the group photo", e);
            }
        }

        if (name != null && !name.trim().isEmpty()) {
            group.setName(name);
        }

        if (description != null && !description.trim().isEmpty()) {
            group.setDescription(description);
        }

        groupRepository.save(group);

        log.info("Group info successfully edited");
        GroupEditResponse response = new GroupEditResponse();
        response.setPhotoUrl(converterService.convertPhotoUriToUrl(group.getPhotoUri()));
        return response;
    }

    @Override
    public LeaderboardResponse getTopScorer(String groupId) {
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group found"));

        MatchOperation matchOperation = Aggregation.match(Criteria.where("groupId").is(groupId));

        AddFieldsOperation convertToObjectId = Aggregation.addFields()
                .addField("userProfileId")
                .withValue(ConvertOperators.ToObjectId.toObjectId("$userProfileId"))
                .build();

        LookupOperation lookupUserProfile = Aggregation.lookup("userProfiles", "userProfileId", "_id", "userProfile");
        UnwindOperation unwindUserProfile = Aggregation.unwind("userProfile");

        AddFieldsOperation convertToString = Aggregation.addFields()
                .addField("userProfileId")
                .withValue(ConvertOperators.ToString.toString("$userProfileId"))
                .build();

        LookupOperation lookupOrders = Aggregation.lookup("orders", "userProfileId", "userProfileId", "orders");

        AddFieldsOperation addOrderCountField = Aggregation.addFields()
                .addField("orderCount")
                .withValueOf(ArrayOperators.Size.lengthOfArray("$orders"))
                .build();

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "userProfile.score"));

        LimitOperation limitOperation = Aggregation.limit(1);

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("_id")
                .and("userProfile.photoUri").as("photoUrl")
                .and("userProfileId").as("userProfileId")
                .and("userProfile.firstName").as("firstName")
                .and("userProfile.lastName").as("lastName")
                .and("groupId").as("groupId")
                .and("userProfile.score").as("score")
                .and("orderCount").as("orderCount");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                convertToObjectId,
                lookupUserProfile,
                unwindUserProfile,
                convertToString,
                lookupOrders,
                addOrderCountField,
                sortOperation,
                limitOperation,
                projectionOperation
        );

        AggregationResults<UserProfileExpandedResponse> results = mongoTemplate.aggregate(
                aggregation, "groupMemberships", UserProfileExpandedResponse.class
        );

        LeaderboardResponse leaderboardResponse = converterService.convertToLeaderboardResponse(results.getUniqueMappedResult());

        log.info("Fetched the top scorer");
        return leaderboardResponse;
    }

    @Override
    public List<GroupDto> getProfileGroups() {

        if (Helper.getLoggedInUserProfileId() == null) {
            throw new IllegalStateException("Bad user profile id present");
        }

        MatchOperation matchOperation = Aggregation.match(Criteria.where("userProfileId").is(Helper.getLoggedInUserProfileId()));

        AddFieldsOperation addFieldsOperation = Aggregation.addFields()
                .addField("groupId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$groupId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("groups", "groupId", "_id", "group");

        UnwindOperation unwindOperation = Aggregation.unwind("group");

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("_id")
                .and("group._id").as("_id")
                .and("group.name").as("name")
                .and("group.description").as("description")
                .and("group.photoUri").as("photoUri");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                addFieldsOperation,
                lookupOperation,
                unwindOperation,
                projectionOperation
        );

        AggregationResults<Group> results = mongoTemplate.aggregate(aggregation, "groupMemberships", Group.class);

        return results.getMappedResults().stream()
                .map(converterService::convertToGroupDto)
                .toList();
    }

    @Override
    public List<GroupMemberResponse> getGroupMembers(String groupId) {
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group associated with the groupId"));

        MatchOperation matchOperation = Aggregation.match(Criteria.where("groupId").is(groupId));

        AddFieldsOperation addFieldsOperation = Aggregation.addFields()
                .addField("userProfileId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$userProfileId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("userProfiles", "userProfileId", "_id", "userProfile");

        UnwindOperation unwindOperation = Aggregation.unwind("userProfile");

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("_id")
                .andInclude("userProfileId")
                .andInclude("roles")
                .and("userProfile.firstName").as("firstName")
                .and("userProfile.lastName").as("lastName")
                .and("userProfile.photoUri").as("photoUrl");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                addFieldsOperation,
                lookupOperation,
                unwindOperation,
                projectionOperation
        );

        AggregationResults<GroupMemberDto> results = mongoTemplate.aggregate(aggregation,
                "groupMemberships", GroupMemberDto.class);


        return results.getMappedResults().stream()
                .map(converterService::convertToGroupMemberResponse)
                .toList();
    }
}
