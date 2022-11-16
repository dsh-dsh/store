package com.example.store.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class Util {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter dateFormatterFourDigitsYear = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalDateTime getLocalDateTime(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDate getLocalDate(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate getLocalDate(String time) {
        return LocalDate.parse(time, dateTimeFormatter);
    }

    public static LocalDateTime getLocalDateTime(String time) {
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    public static long getLongLocalDateTime(LocalDateTime time) {
        return ZonedDateTime.of(time, ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getLongLocalDateTime(String time) {
        return ZonedDateTime.of(LocalDateTime.parse(time, dateTimeFormatter), ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getLongLocalDate(LocalDate time) {
        return time.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static float floorValue(float value, int fractionalLength) {
        float k = (float) Math.pow(10, fractionalLength);
        return (float) Math.floor(value * k) / k;
    }

    public static String getDate(LocalDateTime localDateTime) {
        return localDateTime.format(dateFormatter);
    }

    public static String getDateFourDigitsYear(LocalDateTime localDateTime) {
        return localDateTime.format(dateFormatterFourDigitsYear);
    }

    public static String getDateAndTime(LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }

    public static String getTime(LocalDateTime localDateTime) {
        return localDateTime.format(timeFormatter);
    }

    private Util() {
    }
}
