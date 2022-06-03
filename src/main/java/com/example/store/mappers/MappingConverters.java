package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class MappingConverters {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    protected final Converter<String, LocalDateTime> stringToDateTime = value -> {
        String date = value.getSource();
        if(date != null && !date.equals("")) {
            return LocalDateTime.parse(value.getSource(), timeFormatter);
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

    protected final Converter<LocalDateTime, String> dateTimeConverter =
            date -> {
                LocalDateTime localDateTime = date.getSource();
                return localDateTime == null ? "" : localDateTime.format(timeFormatter);
            };

    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().toString();

    protected final Converter<DocumentType, String> documentTypeStringConverter = type -> type.getSource().toString();

    protected final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType() == DocumentType.CHECK_DOC;

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());
}
