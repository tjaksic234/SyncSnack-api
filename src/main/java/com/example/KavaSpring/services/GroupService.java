package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dto.*;
import com.example.KavaSpring.models.enums.Role;
import com.example.KavaSpring.models.enums.SortCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(GroupRequest request);
    GroupDto getGroupById(String id);
    GroupResponse joinGroup(GroupRequest request);
    List<LeaderboardResponse> getLeaderboard(String groupId, SortCondition condition, Pageable pageable);
    List<GroupOrderCountDto> countGroupOrders(String groupId);
    GroupEditResponse editGroupInfo(String groupId, String name, String description, MultipartFile photoFile);
    LeaderboardResponse getTopScorer(String groupId);
    List<GroupDto> getProfileGroups();
    GroupMembershipDto getGroupRoles(String groupId);
    List<GroupMemberResponse> getGroupMembers(String groupId, Pageable pageable);
    void kickUserFromGroup(String groupId, String userProfileId);
    void promoteUser(String groupId, String userProfileId, Role role);
    String generateInvitation(String groupId, String invitedBy);
    GroupDto joinViaInvitation(String code);
    void leaveGroup(String groupId);
}
