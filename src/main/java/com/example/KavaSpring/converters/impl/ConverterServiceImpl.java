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
    public UserProfileDto convertToUserProfileDto(UserProfile userProfile) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUserId(userProfile.getUserId());
        userProfileDto.setScore(userProfile.getScore());
        userProfileDto.setGroupId(userProfile.getGroupId());
        userProfileDto.setFirstName(userProfile.getFirstName());
        userProfileDto.setLastName(userProfile.getLastName());
        if (userProfile.getPhotoUri() != null && !userProfile.getPhotoUri().isEmpty()) {
            URL presignedUrl = amazonS3Service.generatePresignedUrl(userProfile.getPhotoUri());
            userProfileDto.setPhotoUrl(presignedUrl.toString());
        }
        return userProfileDto;
    }

    @Override
    public UserProfileResponse convertToUserProfileResponse(UserProfileRequest request) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(request.getUserId());
        response.setGroupId(request.getGroupId());
        response.setFirstName(request.getFirstName());
        response.setLastName(request.getLastName());
        return response;
    }

    @Override
    public GroupDto convertToGroupDto(Group group) {
        GroupDto groupDto = new GroupDto();
        groupDto.setName(group.getName());
        groupDto.setDescription(group.getDescription());
        return groupDto;
    }

    @Override
    public GroupResponse convertToGroupResponse(GroupRequest request) {
        GroupResponse response = new GroupResponse();
        response.setName(request.getName());
        response.setDescription(request.getDescription());
        return response;
    }

    @Override
    public GroupMemberResponse convertToGroupMemberResponse(UserProfileExpandedResponse response) {
        GroupMemberResponse groupMember = new GroupMemberResponse();
        groupMember.setUserProfileId(response.getUserProfileId());
        groupMember.setFirstName(response.getFirstName());
        groupMember.setLastName(response.getLastName());
        groupMember.setScore(response.getScore());
        groupMember.setOrderCount(response.getOrderCount());
        groupMember.setPhotoUrl(convertPhotoUriToUrl(response.getPhotoUrl()));
        return groupMember;
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
        notification.setAdditionalOptions(order.getAdditionalOptions());
        notification.setCreatedAt(order.getCreatedAt());
        return notification;
    }

    @Override
    public String convertPhotoUriToUrl(String photoUri) {
        URL presignedUrl = amazonS3Service.generatePresignedUrl(photoUri);
        return presignedUrl.toString();
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
