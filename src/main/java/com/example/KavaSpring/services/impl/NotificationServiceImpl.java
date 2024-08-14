package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.models.dao.Notification;
import com.example.KavaSpring.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @Override
    public List<Notification> getAllNotifications() {

        return List.of();
    }
}
