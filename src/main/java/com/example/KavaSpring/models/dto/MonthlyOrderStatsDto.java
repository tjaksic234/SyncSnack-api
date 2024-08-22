package com.example.KavaSpring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyOrderStatsDto {
    private int year;
    private int month;
    private int count;
}
