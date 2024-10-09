package com.example.KavaSpring.models.dao;

import com.example.KavaSpring.models.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;

@Document(collection = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private String id;

    @NotBlank
    @Size(max = 120)
    private String userProfileId;

    @NotBlank
    private OrderStatus status = OrderStatus.IN_PROGRESS;

    @NotBlank
    private String eventId;

    @NotBlank
    private String groupId;

    private HashMap<String, Object> additionalOptions = new HashMap<>() {{
        put("description", "");
    }};

    private int rating;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime completedAt;
}
