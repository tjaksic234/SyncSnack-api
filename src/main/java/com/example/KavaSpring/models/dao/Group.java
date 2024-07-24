package com.example.KavaSpring.models.dao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {

    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 120)
    private String description;

    @NotBlank
    @Size(max = 120)
    private String password;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    //TODO mozda bude trebalo maknuti group password za daljnji razvoj logike dodavanja usera u grupe
}