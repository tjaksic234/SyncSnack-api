package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {

    Optional<Group> getById(String id);
}
