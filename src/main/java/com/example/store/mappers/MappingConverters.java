package com.example.store.mappers;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.*;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.*;
import com.example.store.repositories.CompanyRepository;
import com.example.store.services.*;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
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
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyRepository companyRepository;


    protected final Converter<Item, Integer> parentConverter = item -> item.getSource().getId();
    protected final Converter<String, PeriodicValueType> typeConverter = str -> PeriodicValueType.valueOf(str.getSource());
    protected final Converter<PaymentType, String> paymentTypeConverter = type -> type.getSource().getValue();
    protected final Converter<DocumentType, String> docTypeConverter = type -> type.getSource().getValue();
    protected final Converter<Item, Integer> itemIdConverter = item -> item.getSource().getId();
    protected final Converter<Item, String> itemNameConverter = item -> item.getSource().getName();
    protected final Converter<Integer, Item> idToItemConverter = dto -> getItem(dto.getSource());
    protected final Converter<EnumDTO, Workshop> workshopDTOConverter = dto -> Workshop.valueOf(dto.getSource().getCode());
    protected final Converter<EnumDTO, Unit> unitDTOConverter = dto -> Unit.valueOf(dto.getSource().getCode());
    protected final Converter<Workshop, EnumDTO> workshopConverter = shop -> getEnumDTO(shop.getSource());
    protected final Converter<Unit, EnumDTO> unitConverter = unit -> getEnumDTO(unit.getSource());

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

    protected final Converter<ItemDoc, List<DocItemDTO>> docItemsConverter =
            itemDoc -> docItemService.getItemDTOListByDoc(itemDoc.getSource()) ;

    protected final Converter<User, String> nameConverter =
            user -> user.getSource().getLastName() + " " + user.getSource().getFirstName();

    protected final Converter<Integer, User> userParentConverter =
            src -> getUser(src.getSource());

    protected final Converter<Integer, User> userParentIdConverter =
            src -> getUserById(src.getSource());

    protected final Converter<Integer, Company> companyParentConverter =
            src -> getCompanyByCode(src.getSource());

    protected final Converter<ItemDoc, CheckInfoDTO> checkInfoConverter =
            doc -> checkInfoService.getCheckInfoDTO(doc.getSource());

    protected final Converter<ItemDoc, Float> docItemAmountConverter =
            doc -> docItemService.getItemsAmount(doc.getSource());

    private EnumDTO getEnumDTO(EnumeratedInterface enumeratedInterface) {
        EnumDTO dto = new EnumDTO();
        dto.setName(enumeratedInterface.getValue());
        dto.setCode(enumeratedInterface.toString());
        return dto;
    }

    private Item getItem(int itemId) {
        if(itemId == 0) return null;
        return itemService.findItemById(itemId);
    }

    private User getUser(int code) {
        if(code == 0) return null;
        return userService.getByCode(code);
    }

    private User getUserById(int userId) {
        if(userId == 0) return null;
        return userService.getById(userId);
    }

    private Company getCompanyByCode(int code) {
        return companyRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_COMPANY_MESSAGE, code)));
    }
}
