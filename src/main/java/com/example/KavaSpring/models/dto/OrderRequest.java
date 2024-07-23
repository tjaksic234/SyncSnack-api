package com.example.KavaSpring.models.dto;

import com.example.KavaSpring.models.enums.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    private String orderedBy;

    private HashMap<String, Object> additionalOptions;

}
