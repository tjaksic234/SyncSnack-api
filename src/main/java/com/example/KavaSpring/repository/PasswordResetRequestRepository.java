package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRequestRepository extends MongoRepository<PasswordResetToken, String> {
}