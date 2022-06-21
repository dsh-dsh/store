package com.example.store.mappers;

import com.example.store.model.dto.*;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.*;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import com.example.store.services.ItemService;
import com.example.store.utils.Constants;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;

@Component
public class MappingConverters {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;
    @Autowired
    private ItemService itemService;

    protected final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType() == DocumentType.CHECK_DOC;

    protected final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();
    protected final Converter<String, QuantityType> typeConverter = str -> QuantityType.valueOf(str.getSource());
    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().toString();
    protected final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    protected final Converter<Item, ItemDTOForIngredient> itemConverter = item -> getItemDTO(item.getSource());
    protected final Converter<ItemDTOForIngredient, Item> itemDTOConverter = dto -> getItem(dto.getSource());

    protected final Converter<EnumDTO, Workshop> workshopDTOConverter = dto -> Workshop.valueOf(dto.getSource().getCode());
    protected final Converter<EnumDTO, Unit> unitDTOConverter = dto -> Unit.valueOf(dto.getSource().getCode());

    protected final Converter<Workshop, EnumDTO> workshopConverter = shop -> {
        EnumDTO dto = new EnumDTO();
        dto.setName(shop.getSource().getValue());
        dto.setCode(shop.getSource().toString());
        return dto;
    };
    protected final Converter<Unit, EnumDTO> unitConverter = unit -> {
        EnumDTO dto = new EnumDTO();
        dto.setName(unit.getSource().getValue());
        dto.setCode(unit.getSource().toString());
        return dto;
    };

    protected final Converter<LocalDateTime, Long> dateTimeToLongConverter =
            time -> ZonedDateTime.of(time.getSource(), ZoneId.systemDefault()).toInstant().toEpochMilli();

    protected final Converter<Long, LocalDateTime> longToDateTimeConverter =
            src -> Instant.ofEpochMilli(src.getSource()).atZone(ZoneId.systemDefault()).toLocalDateTime();

    protected final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            itemDoc -> docItemService.getItemDTOListByDoc(itemDoc.getSource()) ;

    protected final Converter<User, String> nameConverter =
            user -> user.getSource().getLastName() + " " + user.getSource().getFirstName();

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    protected final Converter<String, LocalDateTime> stringToDateTimeConverter = value -> {
        String date = value.getSource();
        if(date != null && !date.equals("")) {
            return LocalDateTime.parse(value.getSource(), Constants.TIME_FORMATTER);
        } else {
            return null;
        }
    };

    protected final Converter<String, LocalDate> stringToDateConverter = value -> {
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
                return localDateTime == null ? "" : localDateTime.format(Constants.TIME_FORMATTER);
            };

    private ItemDTOForIngredient getItemDTO(Item item) {
        ItemDTOForIngredient dto = new ItemDTOForIngredient();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    private Item getItem(ItemDTOForIngredient dto) {
        if(dto == null) return null;
        return itemService.getItemById(dto.getId());
    }
}
