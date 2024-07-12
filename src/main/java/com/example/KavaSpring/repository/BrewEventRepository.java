package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.dao.enums.EventStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BrewEventRepository extends MongoRepository<BrewEvent, String> {
    BrewEvent findByEventId(String eventId);
    Boolean existsByEventId(String eventId);

    // Method to check for active events for a user
    boolean existsByCreator_IdAndStatus(String creator, EventStatus status);

    // Method to edit the brew events
    BrewEvent findByCreatorIdAndEventId(String creatorId, String eventId);

    // Method to check the pending status of the events
    List<BrewEvent> findByStatusAndStartTimeBefore(EventStatus status, LocalDateTime time);

    @Aggregation({
            "{$match: { _id: {$ne: ObjectId('6690c8a718c47237f3b7c666')} }}",
            "{$lookup: { from: 'users', localField: 'id', foreignField: 'creator.$id', as: 'EventDetails' }}"
    })
    List<BrewEvent> findOngoingEvents(String id);
}
