package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<GroupOrderCountDto> countGroupOrders(String groupId);
    void editGroupInfo(String groupId, GroupEditRequest request);
    GroupMemberResponse getTopScorer(String groupId);
}
