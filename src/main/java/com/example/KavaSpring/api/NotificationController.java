package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.models.dao.Notification;
import com.example.KavaSpring.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<?> getAllNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            if (notifications.isEmpty()) {
                log.info("No notifications found");
                return ResponseEntity
                        .status(HttpStatus.NO_CONTENT)
                        .body("No notifications available at this time.");
            }
            log.info("Retrieved {} notifications", notifications.size());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error occurred while fetching notifications", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching notifications: " + e.getMessage());
        }
    }

}
