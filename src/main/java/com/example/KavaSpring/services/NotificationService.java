package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Notification;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications(Pageable pageable);
}
