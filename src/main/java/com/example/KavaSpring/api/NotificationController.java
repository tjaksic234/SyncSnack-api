package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.models.dao.Notification;
import com.example.KavaSpring.services.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("recipient")
    public ResponseEntity<?> getAllNotifications(
            @RequestHeader(value = "groupId") String groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        try {
            List<Notification> notifications = notificationService.getAllNotifications(groupId, PageRequest.of(page, size));
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
