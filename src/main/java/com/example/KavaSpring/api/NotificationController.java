package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.models.dto.NotificationRequest;
import com.example.KavaSpring.models.dto.NotificationResponse;
import com.example.KavaSpring.services.FCMService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/notifications")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class NotificationController {

    private final FCMService fcmService;

    //! zasad ova klasa nema funkcionalnost trenutno se testira
    @PostMapping("/send")
    public ResponseEntity sendNotification(@RequestBody NotificationRequest request) {
        try {
            log.info("Sending notification");
            fcmService.sendNotificationToTopic(request);
            return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
