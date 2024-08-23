package com.example.KavaSpring.security.utils;

import com.example.KavaSpring.models.dto.MonthlyStatsDto;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticUtils {

    public static List<MonthlyStatsDto> fillMissingMonths(List<MonthlyStatsDto> inputStats, LocalDateTime startDate, LocalDateTime endDate) {
        Map<YearMonth, MonthlyStatsDto> statsMap = inputStats.stream()
                .collect(Collectors.toMap(
                        dto -> YearMonth.of(dto.getYear(), dto.getMonth()),
                        dto -> dto
                ));

        List<MonthlyStatsDto> result = new ArrayList<>();
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            MonthlyStatsDto dto = statsMap.getOrDefault(current,
                    new MonthlyStatsDto(current.getYear(), current.getMonthValue(), 0));
            result.add(dto);
            current = current.plusMonths(1);
        }

        return result;
    }
}
