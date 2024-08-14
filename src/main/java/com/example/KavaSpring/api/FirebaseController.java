package com.example.KavaSpring.api;

import com.example.KavaSpring.config.openapi.ShowAPI;
import com.example.KavaSpring.models.dto.MobileNotification;
import com.example.KavaSpring.services.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/mobile")
@Slf4j
@AllArgsConstructor
@ShowAPI
public class FirebaseController {

    private final FirebaseMessagingService firebaseMessagingService;

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification (
            @RequestBody MobileNotification mobileNotification,
            @RequestParam String token
    ) {
        try {
            log.info("Sending notification");
            return ResponseEntity.ok(firebaseMessagingService.sendNotification(mobileNotification, token));
        } catch (FirebaseMessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
