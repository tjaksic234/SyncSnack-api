package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.SortCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<GroupMemberResponse> getLeaderboard(String groupId, SortCondition condition, Pageable pageable);
    List<GroupOrderCountDto> countGroupOrders(String groupId);
    GroupEditResponse editGroupInfo(String groupId, String name, String description, MultipartFile photoFile);
    GroupMemberResponse getTopScorer(String groupId);
    List<GroupDto> getProfileGroups();
}
