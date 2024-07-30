package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dao.Order;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;

import java.util.List;

public interface ConverterService {

    UserProfileDto convertToUserProfileDto(UserProfile userProfile);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
    GroupDto convertToGroupDto(Group group);
    GroupResponse convertToGroupResponse(GroupRequest request);
    EventDto convertToEventDto(Event event);
    EventResponse convertToEventResponse(EventRequest request);
    OrderDto convertToOrderDto(Order order);
    OrderResponse convertToOrderResponse(OrderRequest request);
    OrderActivityResponse convertToOrderActiveResponse(OrderActivityResponse response);
    OrderEventInfoDto convertToOrderEventInfoDto(OrderEventInfoDto dto);
}
