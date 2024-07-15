package com.example.KavaSpring.api.dto;


import com.example.KavaSpring.models.dao.CoffeeOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetBrewEventOrdersResponse {

    @NotBlank
    private LocalDateTime startTime;

    @NotNull
    private List<CoffeeOrder> orders;
}
