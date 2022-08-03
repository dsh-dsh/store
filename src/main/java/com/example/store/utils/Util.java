package com.example.store.utils;

import java.time.*;

public class Util {

    public static LocalDateTime getLocalDateTime(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate getLocalDate(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static long getLongLocalDateTime(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getLongLocalDate(LocalDate time) {
        return time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private Util() {
    }
}
