package com.ojt.klb.utils;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

public class DateFormatter {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            return DATE_TIME_FORMATTER.format(localDateTime);
        }
        return null;
    }
}

