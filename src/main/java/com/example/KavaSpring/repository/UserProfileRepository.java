package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> getUserProfileById(String id);
    boolean existsByUserId(String id);
}
