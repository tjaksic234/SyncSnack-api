package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.GroupAlreadyExistsException;
import com.example.KavaSpring.exceptions.NotFoundException;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

         //? Kreiranje group objekta koji se sprema u bazu
         Group group = new Group();
         group.setName(request.getName());
         group.setDescription(request.getDescription());
         group.setPassword(passwordEncoder.encode(request.getPassword()));
         groupRepository.save(group);

         //? Kreiranje group response objekta
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

        if (!passwordEncoder.matches(request.getPassword(), group.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        GroupResponse response = new GroupResponse();
        response.setGroupId(group.getId());
        response.setName(request.getName());

        log.info("Group join successful");
        return response;
    }

    @Override
    public List<GroupOrderCountDto> countGroupOrders() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        String groupId = userProfile.getGroupId();

        AddFieldsOperation convertUserProfileIdToObjectId = Aggregation.addFields()
                .addField("userProfileId")
                .withValueOf(ConvertOperators.ToObjectId.toObjectId("$userProfileId"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("userProfiles", "userProfileId", "_id", "userProfile");

        UnwindOperation unwindOperation = Aggregation.unwind("userProfile");

        MatchOperation matchByGroupId = Aggregation.match(Criteria.where("userProfile.groupId").is(groupId));

        GroupOperation groupByOrderStatus = Aggregation.group("status").count().as("value");

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("name")
                .andInclude("value")
                .andExclude("_id");

        Aggregation aggregation = Aggregation.newAggregation(
                convertUserProfileIdToObjectId,
                lookupOperation,
                unwindOperation,
                matchByGroupId,
                groupByOrderStatus,
                projectionOperation
        );

        AggregationResults<GroupOrderCountDto> results = mongoTemplate.aggregate(aggregation, "orders", GroupOrderCountDto.class);

        log.info("Fetched the group order count successfully");
        return results.getMappedResults();
    }

    @Override
    public void editGroupInfo(GroupEditRequest request) {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());

        Optional<Group> groupOptional = groupRepository.getById(userProfile.getGroupId());

        if (groupOptional.isEmpty()) {
            throw new NotFoundException("No group associated with the provided id");
        }

        Group group = groupOptional.get();

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        groupRepository.save(group);
        log.info("Group info successfully edited");
    }

    @Override
    public UserProfile getTopScorer() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        String groupId = userProfile.getGroupId();

        if (groupId.isEmpty()) {
            throw new IllegalStateException("No group id associated with the user profile");
        }

        MatchOperation matchOperation = Aggregation.match(Criteria.where("groupId").is(groupId));

        AddFieldsOperation convertToString = Aggregation.addFields()
                .addField("_id")
                .withValueOf(ConvertOperators.ToString.toString("$_id"))
                .build();

        LookupOperation lookupOperation = Aggregation.lookup("orders", "_id", "userProfileId", "orders");

        AddFieldsOperation addOrderCountField = Aggregation.addFields()
                .addField("orderCount")
                .withValueOf(ArrayOperators.Size.lengthOfArray("$orders"))
                .build();

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "score", "orderCount"));

        LimitOperation limitOperation = Aggregation.limit(1);

        ProjectionOperation projectionOperation = Aggregation.project()
                .andExclude("orderCount")
                .andExclude("orders");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                convertToString,
                lookupOperation,
                addOrderCountField,
                sortOperation,
                limitOperation,
                projectionOperation
        );

        AggregationResults<UserProfile> results = mongoTemplate.aggregate(aggregation, "userProfiles", UserProfile.class);

        log.info("Fetched the top scorer");
        return results.getUniqueMappedResult();
    }


}
