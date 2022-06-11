package com.example.store.mappers;

import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.*;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import com.example.store.services.ItemService;
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
    @Autowired
    private ItemService itemService;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");


    protected final Converter<Workshop, String> workshopConverter = shop -> shop.getSource().toString();
    protected final Converter<String, Workshop> stringWorkshopConverter = str -> Workshop.valueOf(str.getSource());
    protected final Converter<Unit, String> unitConverter = unit -> unit.getSource().toString();
    protected final Converter<String, Unit> stringUnitConverter = str -> Unit.valueOf(str.getSource());
    protected final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();
    protected final Converter<String, QuantityType> typeConverter = str -> QuantityType.valueOf(str.getSource());
    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().toString();
    protected final Converter<DocumentType, String> documentTypeStringConverter = type -> type.getSource().toString();
    protected static final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    protected final Converter<Item, ItemDTO> itemConverter = item -> getItemDTO(item.getSource());
    protected final Converter<ItemDTO, Item> itemDTOConverter = dto -> getItem(dto.getSource());

    protected final Converter<User, String> nameConverter =
            user -> user.getSource().getLastName() + " " + user.getSource().getFirstName();

    protected final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType() == DocumentType.CHECK_DOC;

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

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

    private ItemDTO getItemDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    private Item getItem(ItemDTO dto) {
        if(dto == null) return null;
        return itemService.getItemById(dto.getId());
    }
}
