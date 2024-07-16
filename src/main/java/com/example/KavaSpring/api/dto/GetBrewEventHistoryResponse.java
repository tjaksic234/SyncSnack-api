package com.example.KavaSpring.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBrewEventHistoryResponse {

    @NotBlank
    private LocalDateTime startTime;

    @NotNull
    private List<String> orderIds;
}
