package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.GroupDto;
import com.example.KavaSpring.models.dto.GroupOrderCountDto;
import com.example.KavaSpring.models.dto.GroupRequest;
import com.example.KavaSpring.models.dto.GroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<GroupOrderCountDto> countGroupOrders();
}
