package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> getById(String id);
    boolean existsByUserProfileIdAndEventId(String userProfileId, String eventId);
    List<Order> findAllByEventId(String id);
}
