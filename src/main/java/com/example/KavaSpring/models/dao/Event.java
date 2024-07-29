package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.EventStatus;
import com.example.KavaSpring.models.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "events")
@Data
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndex(name = "event_search_index", def = "{'status': 1, 'eventType': 1, 'groupId': 1, 'creatorId': 1, 'createdAt': -1}")
public class Event {

    @Id
    private String id;

    @NotBlank
    private String userProfileId;

    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 120)
    private String description;

    @NotBlank
    private String groupId;

    private EventStatus status = EventStatus.PENDING;

    private EventType eventType;


    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime pendingUntil;

    private LocalDateTime completedAt;
}
