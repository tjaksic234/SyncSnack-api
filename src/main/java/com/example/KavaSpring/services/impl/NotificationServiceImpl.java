package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.Notification;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.security.utils.Helper;
import com.example.KavaSpring.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<Notification> getAllNotifications() {
        UserProfile userProfile = userProfileRepository.getUserProfileByUserId(Helper.getLoggedInUserId());
        List<Notification> notifications = new ArrayList<>();

        //? aggregation for the order notifications
        MatchOperation matchByOrderNotificationAndRecipientId = Aggregation.match(Criteria.where("notificationType").is("ORDER")
                .and("recipientUserProfileId").is(userProfile.getId()));

        ProjectionOperation projectToOrderNotification = Aggregation.project(
                "orderId", "userProfileId", "firstName", "lastName",
                        "eventId", "additionalOptions", "createdAt", "photoUri")
                .and("photoUri").as("profilePhoto")
                .andExclude("_id");

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));

        Aggregation aggregationOrderNotification = Aggregation.newAggregation(
                matchByOrderNotificationAndRecipientId,
                projectToOrderNotification,
                sortOperation
        );

        AggregationResults<Notification> resultsOrderNotifications = mongoTemplate.aggregate(aggregationOrderNotification, "notifications", Notification.class);

        notifications.addAll(resultsOrderNotifications.getMappedResults());


        //? aggregation for the event notifications
        MatchOperation matchByEventNotificationAndGroupId = Aggregation.match(Criteria.where("notificationType").is("EVENT")
                .and("groupId").is(userProfile.getGroupId())
                .and("userProfileId").ne(userProfile.getId()));

        ProjectionOperation projectToEventNotification = Aggregation.project(
                "eventId", "groupId", "firstName", "lastName", "title", "description",
                        "eventType", "createdAt", "pendingUntil", "userProfileId", "photoUri")
                .and("photoUri").as("profilePhoto")
                .andExclude("_id");

        Aggregation aggregationEventNotification = Aggregation.newAggregation(
                matchByEventNotificationAndGroupId,
                projectToEventNotification,
                sortOperation
        );
        AggregationResults<Notification> resultsEventNotifications = mongoTemplate.aggregate(aggregationEventNotification, "notifications", Notification.class);

        notifications.addAll(resultsEventNotifications.getMappedResults());
          notifications.forEach(notification -> {
            if (notification.getPhotoUri() != null) {
                String convertedUrl = converterService.convertPhotoUriToUrl(notification.getPhotoUri());
                notification.setPhotoUri(convertedUrl);
            }
        });
        return notifications;
    }
}
