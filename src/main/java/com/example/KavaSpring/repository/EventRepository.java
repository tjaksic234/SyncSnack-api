package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.Event;
import com.example.KavaSpring.models.enums.EventStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    Optional<Event> getById(String id);
    List<Event> findByUserProfileIdAndStatus(String id, EventStatus status);
}
