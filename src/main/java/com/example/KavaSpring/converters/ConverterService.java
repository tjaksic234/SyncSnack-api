package com.example.KavaSpring.converters;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.dao.Group;
import com.example.KavaSpring.models.dao.UserProfile;
import com.example.KavaSpring.models.dto.*;

public interface ConverterService {

    UserProfileDto convertToUserProfileDto(UserProfile userProfile);
    UserProfileResponse convertToUserProfileResponse(UserProfileRequest request);
    GroupDto convertToGroupDto(Group group);
    GroupResponse convertToGroupResponse(GroupRequest request);
    EventDto convertToEventDto(Event event);
    EventResponse convertToEventResponse(EventRequest request);
}
