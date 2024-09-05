package com.example.KavaSpring.models.enums;

import java.time.temporal.TemporalAdjusters;
import java.time.LocalDateTime;
import java.time.DayOfWeek;

public enum TimeFilter {
    TODAY,
    TOMORROW,
    THIS_WEEK,
    THIS_MONTH;

    public LocalDateTime getStartDate(LocalDateTime now) {
        return switch (this) {
            case TODAY -> now.toLocalDate().atStartOfDay();
            case TOMORROW -> now.toLocalDate().plusDays(1).atStartOfDay();
            case THIS_WEEK -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case THIS_MONTH -> now.with(TemporalAdjusters.firstDayOfMonth());
        };
    }

    public LocalDateTime getEndDate(LocalDateTime now) {
        return switch (this) {
            case TODAY -> now.toLocalDate().plusDays(1).atStartOfDay();
            case TOMORROW -> now.toLocalDate().plusDays(2).atStartOfDay();
            case THIS_WEEK -> now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            case THIS_MONTH -> now.with(TemporalAdjusters.firstDayOfNextMonth());
        };
    }
}
