package com.example.KavaSpring.repository;

import com.example.KavaSpring.models.dao.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
}
