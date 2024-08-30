package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.GroupMembership;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupMembershipRepository extends MongoRepository<GroupMembership, String> {
    List<GroupMembership> findAllByUserProfileId(String userProfileId);
    GroupMembership findByUserProfileIdAndGroupId(String userProfileId, String groupId);
}
