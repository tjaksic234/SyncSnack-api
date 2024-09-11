package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.*;
import com.example.KavaSpring.models.dto.*;

public interface ConverterService {
    UserDto convertToUserDto(User user);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
    GroupDto convertToGroupDto(Group group);
    LeaderboardResponse convertToLeaderboardResponse(UserProfileExpandedResponse response);
    GroupMemberResponse convertToGroupMemberResponse(GroupMemberDto dto);
    GroupMembershipDto convertToGroupMembershipDto(GroupMembership membership);
    EventDto convertToEventDto(Event event);
    EventResponse convertToEventResponse(EventRequest request);
    EventExpandedResponse convertToEventExpandedResponse(Event event);
    EventNotification convertEventToEventNotification(Event event);
    OrderDto convertToOrderDto(Order order);
    OrderResponse convertToOrderResponse(OrderRequest request);
    OrderActivityResponse convertToOrderActiveResponse(OrderActivityResponse response);
    OrderEventInfoDto convertToOrderEventInfoDto(OrderEventInfoDto dto);
    OrderNotification convertOrderToOrderNotification(Order order);
    String convertPhotoUriToUrl(String photoUri);
    Notification convertOrderNotificationToNotification(OrderNotification orderNotification, String recipientUserProfileId);
    Notification convertEventNotificationToNotification(EventNotification eventNotification);
}
