package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.EventType;
import com.example.KavaSpring.models.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;

@Document(collection = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification {

    @Id
    private String id;

    private NotificationType notificationType;

    //? Common fields
    private String userProfileId;
    private String firstName;
    private String lastName;
    private String eventId;
    private String photoUri;

    //? Event notification specific fields
    private String groupId;
    private String title;
    private String description;
    private EventType eventType;
    private LocalDateTime pendingUntil;

    //? Order notification specific fields
    private String orderId;
    private String recipientUserProfileId;
    private HashMap<String, Object> additionalOptions;

    @CreatedDate
    private LocalDateTime createdAt;
}
