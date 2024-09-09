package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.GroupInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends MongoRepository<GroupInvitation, String> {
    Optional<GroupInvitation> findByCodeAndActiveIsTrue(String code);
}
