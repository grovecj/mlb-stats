package com.mlbstats.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter MLB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtils() {
    }

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(MLB_DATE_FORMAT) : null;
    }

    public static LocalDate parseDate(String dateString) {
        return dateString != null ? LocalDate.parse(dateString, MLB_DATE_FORMAT) : null;
    }

    public static int getCurrentSeason() {
        LocalDate now = LocalDate.now();
        // MLB season typically starts in late March
        // If we're before March, use previous year
        if (now.getMonthValue() < 3) {
            return now.getYear() - 1;
        }
        return now.getYear();
    }
}
