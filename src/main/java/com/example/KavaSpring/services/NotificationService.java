package com.example.KavaSpring.services;

import com.example.KavaSpring.models.dao.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications();
}
