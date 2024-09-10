package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.exceptions.NoGroupFoundException;
import com.example.KavaSpring.models.dao.Notification;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.repository.GroupRepository;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final UserProfileRepository userProfileRepository;

    private final MongoTemplate mongoTemplate;

    private final ConverterService converterService;

    private final GroupRepository groupRepository;

    @Override
    public List<Notification> getAllNotifications(String groupId, Pageable pageable) {
        groupRepository.findById(groupId).orElseThrow(() -> new NoGroupFoundException("No group associated with the groupId"));
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        List<Notification> notifications = new ArrayList<>();

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        //? aggregation for the different types of notifications
        MatchOperation matchOperation = Aggregation.match(
                new Criteria().orOperator(
                        Criteria.where("notificationType").is("ORDER")
                                .and("recipientUserProfileId").is(userProfile.getId()),
                        Criteria.where("notificationType").is("EVENT")
                                .and("userProfileId").ne(userProfile.getId())
                )
        );

        ProjectionOperation projectOperation = Aggregation.project()
                .andInclude("notificationType", "orderId", "userProfileId", "firstName", "lastName",
                        "eventId", "additionalOptions", "createdAt", "photoUri", "groupId",
                        "title", "description", "eventType", "pendingUntil")
                .and("photoUri").as("profilePhoto")
                .andExclude("_id");

        SkipOperation skipOperation = Aggregation.skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Aggregation aggregation  = Aggregation.newAggregation(
                matchOperation,
                projectOperation,
                sortOperation,
                skipOperation,
                limitOperation
        );

        AggregationResults<Notification> results = mongoTemplate.aggregate(aggregation , "notifications", Notification.class);
        notifications.addAll(results.getMappedResults());


        notifications.forEach(notification -> {
            if (notification.getPhotoUri() != null) {
                String convertedUrl = converterService.convertPhotoUriToUrl(notification.getPhotoUri());
                notification.setPhotoUri(convertedUrl);
            }
        });
        return notifications;
    }
}
