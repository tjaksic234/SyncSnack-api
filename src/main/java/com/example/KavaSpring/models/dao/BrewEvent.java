package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
    @DBRef
    private User creator;

    @NotNull
    @Indexed
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    @Indexed
    private EventStatus status;

    @NotEmpty
    @Min(0)
    private int pendingTime;

    private List<CoffeeOrder> orders;

    public BrewEvent() {
    }

    public BrewEvent(User creator, int pendingTime) {
        this.creator = creator;
        this.startTime = LocalDateTime.now().plusMinutes(pendingTime);
        this.endTime = null;
        this.status = EventStatus.PENDING;
        this.orders = new ArrayList<>();
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
