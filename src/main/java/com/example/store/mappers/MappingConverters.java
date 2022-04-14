package com.example.store.mappers;

import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class MappingConverters {

    protected final Converter<String, LocalDateTime> stringToDateTime = value -> {
        String date = value.getSource();
        if(date != null && !date.equals("")) {
            return LocalDateTime.parse(value.getSource());
        } else {
            return null;
        }
    };

    protected final Converter<String, LocalDate> stringToDate = value -> {
        String date = value.getSource();
        if(date != null && !date.equals("")) {
            return LocalDate.parse(value.getSource());
        } else {
            return null;
        }
    };

    protected final Converter<LocalDateTime, Long> dateTimeConverter =
            date -> {
                LocalDateTime localDateTime = date.getSource();
                return localDateTime == null ? 0 : localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            };

    protected final Converter<LocalDate, Long> dateConverter =
            date -> {
                LocalDate localDate = date.getSource();
                return localDate == null ? 0 : localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            };

    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().toString();

    protected final Converter<DocumentType, String> documentTypeStringConverter = type -> type.getSource().toString();
}
