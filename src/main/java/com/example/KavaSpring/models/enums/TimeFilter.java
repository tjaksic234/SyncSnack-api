package com.example.KavaSpring.models.enums;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public enum TimeFilter {
    TODAY(0),
    TOMORROW(1),
    THIS_WEEK(7),
    THIS_MONTH(30);

    private final int days;

    public LocalDateTime getStartDate(LocalDateTime now) {
        return now.minusDays(days).withHour(0).withMinute(0).withSecond(0);
    }

    public LocalDateTime getEndDate(LocalDateTime now) {
        return this == TOMORROW ? now.withHour(0).withMinute(0).withSecond(0) : now;
    }
}
