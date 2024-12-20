package com.example.KavaSpring.converters.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.NotificationType;
import com.example.KavaSpring.repository.UserProfileRepository;
import com.example.KavaSpring.services.AmazonS3Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConverterServiceImpl implements ConverterService {

    private final UserProfileRepository userProfileRepository;

    private final AmazonS3Service amazonS3Service;

    @Override
    public UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setVerified(user.isVerified());
        return userDto;
    }

    @Override
    public UserProfileResponse convertToUserProfileResponse(UserProfileRequest request) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(request.getUserId());
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        return response;
    }

    @Override
    public GroupDto convertToGroupDto(Group group) {
        GroupDto groupDto = new GroupDto();
        groupDto.setGroupId(group.getId());
        groupDto.setName(group.getName());
        groupDto.setDescription(group.getDescription());
        groupDto.setPhotoUrl(convertPhotoUriToUrl(group.getPhotoUri()));
        return groupDto;
    }

    @Override
    public LeaderboardResponse convertToLeaderboardResponse(UserProfileExpandedResponse response) {
        LeaderboardResponse leaderboardResponse = new LeaderboardResponse();
        leaderboardResponse.setUserProfileId(response.getUserProfileId());
        leaderboardResponse.setFirstName(response.getFirstName());
        leaderboardResponse.setLastName(response.getLastName());
        leaderboardResponse.setScore(response.getScore());
        leaderboardResponse.setOrderCount(response.getOrderCount());
        leaderboardResponse.setPhotoUrl(convertPhotoUriToUrl(response.getPhotoUrl()));
        return leaderboardResponse;
    }

    @Override
    public GroupMemberResponse convertToGroupMemberResponse(GroupMemberDto dto) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setUserProfileId(dto.getUserProfileId());
        response.setFirstName(dto.getFirstName());
        response.setLastName(dto.getLastName());
        response.setRoles(dto.getRoles());
        response.setPhotoUrl(convertPhotoUriToUrl(dto.getPhotoUrl()));
        return response;
    }

    @Override
    public GroupMembershipDto convertToGroupMembershipDto(GroupMembership membership) {
        GroupMembershipDto groupMembershipDto = new GroupMembershipDto();
        groupMembershipDto.setRoles(membership.getRoles());
        return groupMembershipDto;
    }

    @Override
    public EventDto convertToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setEventId(event.getId());
        eventDto.setUserProfileId(event.getUserProfileId());
        eventDto.setTitle(event.getTitle());
        eventDto.setDescription(event.getDescription());
        eventDto.setGroupId(event.getGroupId());
        eventDto.setStatus(event.getStatus());
        eventDto.setEventType(event.getEventType());
        eventDto.setCreatedAt(event.getCreatedAt());
        eventDto.setPendingUntil(event.getPendingUntil());
        return eventDto;
    }

    @Override
    public EventResponse convertToEventResponse(EventRequest request) {
        EventResponse response = new EventResponse();
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setEventType(request.getEventType());
        response.setPendingTime(request.getPendingTime());
        return response;
    }

    @Override
    public EventExpandedResponse convertToEventExpandedResponse(Event event) {
        EventExpandedResponse response = new EventExpandedResponse();
        Optional<UserProfile> userProfile = userProfileRepository.findById(event.getUserProfileId());
        if (userProfile.isPresent()) {
            response.setUserProfileFirstName(userProfile.get().getFirstName());
            response.setUserProfileLastName(userProfile.get().getLastName());
            response.setPhotoUrl(convertPhotoUriToUrl(userProfile.get().getPhotoUri()));
        }
        response.setEventId(event.getId());
        response.setUserProfileId(event.getUserProfileId());
        response.setTitle(event.getTitle());
        response.setDescription(event.getDescription());
        response.setGroupId(event.getGroupId());
        response.setStatus(event.getStatus());
        response.setEventType(event.getEventType());
        response.setCreatedAt(event.getCreatedAt());
        response.setPendingUntil(event.getPendingUntil());
        return response;
    }

    @Override
    public EventNotification convertEventToEventNotification(Event event) {
        EventNotification notification = new EventNotification();
        Optional<UserProfile> userProfile = userProfileRepository.findById(event.getUserProfileId());
        if (userProfile.isPresent()) {
            notification.setFirstName(userProfile.get().getFirstName());
            notification.setLastName(userProfile.get().getLastName());
            notification.setUserProfileId(event.getUserProfileId());
        }
        userProfile.ifPresent(profile -> notification.setProfilePhoto(convertPhotoUriToUrl(profile.getPhotoUri())));
        notification.setEventId(event.getId());
        notification.setGroupId(event.getGroupId());
        notification.setTitle(event.getTitle());
        notification.setDescription(event.getDescription());
        notification.setEventType(event.getEventType());
        notification.setCreatedAt(event.getCreatedAt());
        notification.setPendingUntil(event.getPendingUntil());
        return notification;
    }

    @Override
    public OrderDto convertToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUserProfileId(order.getUserProfileId());
        orderDto.setStatus(order.getStatus());
        orderDto.setAdditionalOptions(order.getAdditionalOptions());
        orderDto.setRating(order.getRating());
        orderDto.setCreatedAt(order.getCreatedAt());
        return orderDto;
    }

    @Override
    public OrderResponse convertToOrderResponse(OrderRequest request) {
        OrderResponse response = new OrderResponse();
        response.setEventId(request.getEventId());
        response.setAdditionalOptions(request.getAdditionalOptions());
        return response;
    }

    @Override
    public OrderActivityResponse convertToOrderActiveResponse(OrderActivityResponse order) {
        OrderActivityResponse response = new OrderActivityResponse();
        response.setEventId(order.getEventId());
        response.setOrderId(order.getOrderId());
        response.setTitle(order.getTitle());
        response.setDescription(order.getDescription());
        response.setGroupId(order.getGroupId());
        response.setStatus(order.getStatus());
        response.setEventType(order.getEventType());
        response.setCreatedAt(order.getCreatedAt());
        response.setPendingUntil(order.getPendingUntil());
        return response;
    }

    @Override
    public OrderEventInfoDto convertToOrderEventInfoDto(OrderEventInfoDto dto) {
        OrderEventInfoDto orderEventInfoDto = new OrderEventInfoDto();
        orderEventInfoDto.setOrderId(dto.getOrderId());
        orderEventInfoDto.setEventId(dto.getEventId());
        orderEventInfoDto.setGroupId(dto.getGroupId());
        orderEventInfoDto.setEventType(dto.getEventType());
        orderEventInfoDto.setStatus(dto.getStatus());
        orderEventInfoDto.setAdditionalOptions(dto.getAdditionalOptions());
        orderEventInfoDto.setRating(dto.getRating());
        orderEventInfoDto.setCreatedAt(dto.getCreatedAt());
        return orderEventInfoDto;
    }

    @Override
    public OrderNotification convertOrderToOrderNotification(Order order) {
        OrderNotification notification = new OrderNotification();
        Optional<UserProfile> userProfile = userProfileRepository.findById(order.getUserProfileId());
        if (userProfile.isPresent()) {
            notification.setFirstName(userProfile.get().getFirstName());
            notification.setLastName(userProfile.get().getLastName());
        }
        userProfile.ifPresent(profile -> notification.setProfilePhoto(convertPhotoUriToUrl(profile.getPhotoUri())));
        notification.setOrderId(order.getId());
        notification.setUserProfileId(order.getUserProfileId());
        notification.setEventId(order.getEventId());
        notification.setGroupId(order.getGroupId());
        notification.setAdditionalOptions(order.getAdditionalOptions());
        notification.setCreatedAt(order.getCreatedAt());
        return notification;
    }

    @Override
    public String convertPhotoUriToUrl(String photoUri) {
        if (photoUri != null && !photoUri.isEmpty()) {
            URL presignedUrl = amazonS3Service.generatePresignedUrl(photoUri);
            return presignedUrl.toString();
        } else {
            return null;
        }
    }

    @Override
    public Notification convertOrderNotificationToNotification(OrderNotification orderNotification, String recipientUserProfileId) {
        Notification notification = new Notification();
        Optional<UserProfile> userProfile = userProfileRepository.findById(orderNotification.getUserProfileId());
        if (userProfile.isPresent()) {
            notification.setFirstName(userProfile.get().getFirstName());
            notification.setLastName(userProfile.get().getLastName());
            notification.setPhotoUri(userProfile.get().getPhotoUri());
        }
        notification.setOrderId(orderNotification.getOrderId());
        notification.setRecipientUserProfileId(recipientUserProfileId);
        notification.setUserProfileId(orderNotification.getUserProfileId());
        notification.setEventId(orderNotification.getEventId());
        notification.setAdditionalOptions(orderNotification.getAdditionalOptions());
        notification.setNotificationType(NotificationType.ORDER);
        return notification;
    }

    @Override
    public Notification convertEventNotificationToNotification(EventNotification eventNotification) {
        Notification notification = new Notification();
        Optional<UserProfile> userProfile = userProfileRepository.findById(eventNotification.getUserProfileId());
        userProfile.ifPresent(profile -> notification.setPhotoUri(profile.getPhotoUri()));
        notification.setEventId(eventNotification.getEventId());
        notification.setGroupId(eventNotification.getGroupId());
        notification.setUserProfileId(eventNotification.getUserProfileId());
        notification.setFirstName(eventNotification.getFirstName());
        notification.setLastName(eventNotification.getLastName());
        notification.setTitle(eventNotification.getTitle());
        notification.setDescription(eventNotification.getDescription());
        notification.setEventType(eventNotification.getEventType());
        notification.setPendingUntil(eventNotification.getPendingUntil());
        notification.setUserProfileId(eventNotification.getUserProfileId());
        notification.setNotificationType(NotificationType.EVENT);
        return notification;
    }
}
