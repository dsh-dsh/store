package com.example.store.mappers;

import com.example.store.model.dto.EnumDTO;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.*;
import com.example.store.utils.Util;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class MappingConverters {

    protected final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();
    protected final Converter<String, PeriodicValueType> typeConverter = str -> PeriodicValueType.valueOf(str.getSource());
    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().getValue();
    protected final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    protected final Converter<CheckPaymentType, String> checkPaymentTypeConverter = type -> type.getSource().getValue();
    protected final Converter<Item, Integer> itemIdConverter = item -> item.getSource().getId();
    protected final Converter<Item, String> itemNameConverter = item -> item.getSource().getName();
    protected final Converter<EnumDTO, Workshop> workshopDTOConverter = dto -> Workshop.valueOf(dto.getSource().getCode());
    protected final Converter<EnumDTO, Unit> unitDTOConverter = dto -> Unit.valueOf(dto.getSource().getCode());
    protected final Converter<Workshop, EnumDTO> workshopConverter = shop -> getEnumDTO(shop.getSource());
    protected final Converter<Unit, EnumDTO> unitConverter = unit -> getEnumDTO(unit.getSource());
    protected final Converter<Company, String> supplierConverter = supplier -> supplier.getSource().getName();

    protected final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType() == DocumentType.CHECK_DOC;

    protected final Converter<LocalDateTime, Long> dateTimeToLongConverter =
            time -> ZonedDateTime.of(time.getSource(), ZoneId.systemDefault()).toInstant().toEpochMilli();

    protected final Converter<LocalDate, Long> dateToLongConverter =
            time -> time.getSource().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

    protected final Converter<Long, LocalDateTime> longToDateTimeConverter =
            src -> Util.getLocalDateTime(src.getSource());

    protected final Converter<Long, LocalDate> longToDateConverter =
            src -> Util.getLocalDate(src.getSource());

    protected final Converter<User, String> nameConverter =
            user -> user.getSource().getLastName() + " " + user.getSource().getFirstName();

    private EnumDTO getEnumDTO(EnumeratedInterface enumeratedInterface) {
        EnumDTO dto = new EnumDTO();
        dto.setName(enumeratedInterface.getValue());
        dto.setCode(enumeratedInterface.toString());
        return dto;
    }
}
