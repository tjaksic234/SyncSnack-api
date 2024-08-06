package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.VerificationInvitation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationInvitationRepository extends MongoRepository<VerificationInvitation, String> {
}
