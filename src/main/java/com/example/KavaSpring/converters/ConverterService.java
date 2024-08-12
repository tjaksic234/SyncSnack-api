package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.*;

import java.util.List;

public interface ConverterService {

    UserDto convertToUserDto(User user);
    UserProfileDto convertToUserProfileDto(UserProfile userProfile);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
    GroupDto convertToGroupDto(Group group);
    GroupResponse convertToGroupResponse(GroupRequest request);
    EventDto convertToEventDto(Event event);
    EventResponse convertToEventResponse(EventRequest request);
    EventExpandedResponse convertToEventExpandedResponse(Event event);
    OrderDto convertToOrderDto(Order order);
    OrderResponse convertToOrderResponse(OrderRequest request);
    OrderActivityResponse convertToOrderActiveResponse(OrderActivityResponse response);
    OrderEventInfoDto convertToOrderEventInfoDto(OrderEventInfoDto dto);
    OrderSearchResponse convertOrderToOrderSearchResponse(Order order);
}
