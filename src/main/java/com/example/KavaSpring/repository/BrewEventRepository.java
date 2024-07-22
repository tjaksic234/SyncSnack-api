package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.BrewEvent;
import com.example.KavaSpring.models.enums.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BrewEventRepository extends MongoRepository<BrewEvent, String> {

    BrewEvent findByEventId(String eventId);
    Boolean existsByEventId(String eventId);

    // Method to check for active events for a user
    boolean existsByUserIdAndStatus(String userId, OrderStatus status);

    // Method to edit the brew events
    BrewEvent findByUserIdAndEventId(String userId, String eventId);

    // Method to check the pending status of the events
    List<BrewEvent> findByStatusAndStartTimeBefore(OrderStatus status, LocalDateTime time);

    // Method to get all brew events by status
    List<BrewEvent> findByStatus(OrderStatus status);

    // Method to retrieve the brew event associated with the creator of the event
    BrewEvent findByUserIdAndStatus(String userId, OrderStatus status);

    BrewEvent findByUserId(String userId);

    BrewEvent findByUserIdAndOrderIdsContaining(String userId, String orderId);
}
