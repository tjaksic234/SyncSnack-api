package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.*;

public interface ConverterService {

    UserDto convertToUserDto(User user);
    UserProfileDto convertToUserProfileDto(UserProfile userProfile);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
    GroupDto convertToGroupDto(Group group);
    GroupResponse convertToGroupResponse(GroupRequest request);
    GroupMemberResponse convertToGroupMemberResponse(UserProfileExpandedResponse response);
    EventDto convertToEventDto(Event event);
    EventResponse convertToEventResponse(EventRequest request);
    EventExpandedResponse convertToEventExpandedResponse(Event event);
    EventNotification convertEventToEventNotification(Event event);
    OrderDto convertToOrderDto(Order order);
    OrderResponse convertToOrderResponse(OrderRequest request);
    OrderActivityResponse convertToOrderActiveResponse(OrderActivityResponse response);
    OrderEventInfoDto convertToOrderEventInfoDto(OrderEventInfoDto dto);
    OrderSearchResponse convertOrderToOrderSearchResponse(Order order);
    OrderNotification convertOrderToOrderNotification(Order order);
    String convertPhotoUriToUrl(String photoUri);
    Notification convertOrderNotificationToNotification(OrderNotification orderNotification, String recipientUserProfileId);
    Notification convertEventNotificationToNotification(EventNotification eventNotification);
}
