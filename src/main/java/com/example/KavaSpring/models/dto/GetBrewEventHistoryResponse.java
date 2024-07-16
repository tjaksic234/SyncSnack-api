package com.example.KavaSpring.models.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBrewEventHistoryResponse {

    @NotNull
    private List<String> orderIds;
}
