package com.example.KavaSpring.service;

import com.example.KavaSpring.models.dto.GroupDto;
import com.example.KavaSpring.models.dto.GroupRequest;
import com.example.KavaSpring.models.dto.GroupResponse;

public interface GroupService {

    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
}
