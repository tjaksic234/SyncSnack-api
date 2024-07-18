package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "brewEvents")
@Getter @Setter
public class BrewEvent {

    @Id
    private String eventId;

    @NotBlank
    private String userId;

    @NotNull
    @Indexed
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    @Indexed
    private EventStatus status;

    @Min(0)
    private int pendingTime;

    private List<String> orderIds;

    public BrewEvent() {
    }

    public BrewEvent(String userId, int pendingTime) {
        this.userId = userId;
        this.startTime = LocalDateTime.now().plusMinutes(pendingTime);
        this.endTime = null;
        this.pendingTime = pendingTime;
        this.status = EventStatus.PENDING;
        this.orderIds = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", userId='" + userId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }

}
