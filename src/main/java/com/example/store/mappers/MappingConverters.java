package com.example.store.mappers;

import com.example.store.model.dto.*;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.*;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import com.example.store.services.ItemRestService;
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
    @Autowired
    private ItemRestService itemRestService;

    protected final Condition<Document, Document> isCheck =
            doc -> doc.getSource().getDocType() == DocumentType.CHECK_DOC;

    protected final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();
    protected final Converter<String, QuantityType> typeConverter = str -> QuantityType.valueOf(str.getSource());
    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().toString();
    protected final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    protected final Converter<Item, ItemDTOForIngredient> itemConverter = item -> getItemDTO(item.getSource());
    protected final Converter<Integer, Item> idToItemConverter = dto -> getItem(dto.getSource());
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

    protected final Converter<LocalDate, Long> dateToLongConverter =
            time -> time.getSource().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

    protected final Converter<Long, LocalDateTime> longToDateTimeConverter =
            src -> Instant.ofEpochMilli(src.getSource()).atZone(ZoneId.systemDefault()).toLocalDateTime();

    protected final Converter<Long, LocalDate> longToDateConverter =
            src -> Instant.ofEpochMilli(src.getSource()).atZone(ZoneId.systemDefault()).toLocalDate();

    protected final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            itemDoc -> docItemService.getItemDTOListByDoc(itemDoc.getSource()) ;

    protected final Converter<User, String> nameConverter =
            user -> user.getSource().getLastName() + " " + user.getSource().getFirstName();

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    protected final Converter<ItemDoc, Float> docItemAmountConverter =
            doc -> docItemService.getItemsAmount(doc.getSource());

    protected final Converter<Item, List<RestDTO>> restConverter =
            item -> itemRestService.getItemRestList(item.getSource());

    private ItemDTOForIngredient getItemDTO(Item item) {
        ItemDTOForIngredient dto = new ItemDTOForIngredient();
        dto.setId(item.getId());
        dto.setName(item.getName());
        return dto;
    }

    private Item getItem(int itemId) {
        if(itemId == 0) return null;
        return itemService.getItemById(itemId);
    }
}
