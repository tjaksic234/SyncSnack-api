package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "brewEvents")
@Getter @Setter
public class BrewEvent {

    @Id
    private String eventId;

    @NotBlank
    @DBRef
    private User creator;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    private EventStatus status;

    public BrewEvent() {
    }

    public BrewEvent(User creator, LocalDateTime endTime) {
        this.creator = creator;
        this.startTime = LocalDateTime.now() ;
        this.endTime = endTime;
        this.status = EventStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", creatorId='" + creator + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }

}
