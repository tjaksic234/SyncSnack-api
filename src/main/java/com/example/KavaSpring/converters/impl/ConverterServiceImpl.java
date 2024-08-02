package com.example.KavaSpring.converters.impl;

import com.example.KavaSpring.converters.ConverterService;
import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConverterServiceImpl implements ConverterService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfileDto convertToUserProfileDto(UserProfile userProfile) {
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUserId(userProfile.getUserId());
        userProfileDto.setScore(userProfile.getScore());
        userProfileDto.setGroupId(userProfile.getGroupId());
        userProfileDto.setFirstName(userProfile.getFirstName());
        userProfileDto.setLastName(userProfile.getLastName());
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
}
