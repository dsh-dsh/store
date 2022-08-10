package com.example.store.utils;

import java.time.*;

public class Util {

    public static LocalDateTime getLocalDateTime(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate getLocalDate(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate getLocalDate(String time) {
        return LocalDate.parse(time);
    }

    public static long getLongLocalDateTime(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getLongLocalDate(LocalDate time) {
        return time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static float floorValue(float value, int k) {
        return (float) Math.floor(value * k) / k;
    }

    private Util() {
    }
}
