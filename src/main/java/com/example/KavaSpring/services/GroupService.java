package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<GroupMemberResponse> getLeaderboard(String groupId, SortCondition condition, Pageable pageable);
    List<GroupOrderCountDto> countGroupOrders(String groupId);
    void editGroupInfo(String groupId, GroupEditRequest request);
    GroupMemberResponse getTopScorer(String groupId);
    List<GroupDto> getProfileGroups();
}
