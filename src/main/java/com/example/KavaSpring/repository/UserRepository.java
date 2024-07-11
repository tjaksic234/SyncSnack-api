package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    List<User> getAllBy();
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
}
