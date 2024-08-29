package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> getUserProfileById(String id);
    Optional<UserProfile> findByUserId(String id);
    boolean existsByUserId(String id);
    UserProfile getUserProfileByUserId(String id);
    boolean existsById(String id);
    Optional<UserProfile> findById(String id);
    List<UserProfile> findByGroupIdAndFcmTokenIsNotNull(String groupId);
}
