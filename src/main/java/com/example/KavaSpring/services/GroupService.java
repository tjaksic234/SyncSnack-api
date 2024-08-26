package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<GroupOrderCountDto> countGroupOrders();
    void editGroupInfo(GroupEditRequest request);
}
