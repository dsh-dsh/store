package com.example.store.services;

import com.example.store.controllers.TestService;
import com.example.store.model.dto.*;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.QuantityType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ItemServiceTest extends TestService {

    private static final int ITEM_ID = 4;
    private static final int PARENT_ID = 1;
    private static final int SET_ID = 9;
    private static final int NEW_ITEM_ID = 10;
    private static final String EXISTING_ITEM_NAME = "Картофель фри (1)";
    private static final float RETAIL_PRICE_VALUE = 200.00f;
    private static final float DELIVERY_PRICE_VALUE = 250.00f;
    private static final String NEW_ITEM_NAME = "Новое блюдо";
    private static final long DATE = 1643662800000L; // 2022-02-01
    private static final String UPDATE_NAME = "Пиво";
    private static final  long UPDATE_DATE = 1642194000000L; // 2022-01-15
    private static final  long PRICE_DATE = 1640984400000L; // 2022-01-01

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Autowired
    private ItemService itemService;

    @Test
    void getItemDTOTreeTest() {
        List<ItemDTOForTree> list = itemService.getItemDTOTree();
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNewItemTest() {
        int number = 123456789;
        ItemDTO itemDTO = getItemDTO(PRICE_DATE);
        itemDTO.setNumber(number);
        itemDTO.setSets(List.of(9));
        itemDTO.setIngredients(getIngredientDTOList());
        itemService.setNewItem(itemDTO);
        assertTrue(itemService.findItemByNumber(number).isPresent());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateItem() {
//        int number = 123456789;
//        ItemDTO itemDTO = getItemDTO();
//        itemDTO.setNumber(number);
//        itemDTO.setSets(List.of(9));
//        itemDTO.setIngredients(getIngredientDTOList());
//        itemService.updateItem(itemDTO, );
//        assertTrue(itemService.findItemByNumber(number).isPresent());
    }

    @Test
    void updateItemFields() {
    }

    @Test
    void getItemById() {
    }

    @Test
    void findItemByNumber() {
    }

    @Test
    void getItemIdByNumber() {
    }

    @Test
    void getItemDTOById() {
    }

    @Test
    void softDeleteItem() {
    }

    @Test
    void findItemById() {
    }

    @Test
    void getParent() {
    }

    @Test
    void setItemDTOList() {
    }

    @Test
    void getItemDTOForList() {
    }


    private List<IngredientDTO> getIngredientDTOList() {
        QuantityDTO netDTO = new QuantityDTO();
        netDTO.setDate(convertDate(LocalDate.now()));
        netDTO.setType(QuantityType.NET.toString());
        netDTO.setQuantity(0.3f);

        QuantityDTO grossDTO = new QuantityDTO();
        grossDTO.setDate(convertDate(LocalDate.now()));
        grossDTO.setType(QuantityType.GROSS.toString());
        grossDTO.setQuantity(0.2f);

        QuantityDTO enableDTO = new QuantityDTO();
        enableDTO.setDate(convertDate(LocalDate.now()));
        enableDTO.setType(QuantityType.ENABLE.toString());
        enableDTO.setQuantity(1f);

        IngredientDTO first = IngredientDTO.builder()
                .childId(8)
                .netto(netDTO)
                .gross(grossDTO)
                .enable(enableDTO)
                .build();

        netDTO = new QuantityDTO();
        netDTO.setDate(convertDate(LocalDate.now()));
        netDTO.setType(QuantityType.NET.toString());
        netDTO.setQuantity(0.4f);

        grossDTO = new QuantityDTO();
        grossDTO.setDate(convertDate(LocalDate.now()));
        grossDTO.setType(QuantityType.GROSS.toString());
        grossDTO.setQuantity(0.3f);

        enableDTO = new QuantityDTO();
        enableDTO.setDate(convertDate(LocalDate.now()));
        enableDTO.setType(QuantityType.ENABLE.toString());
        enableDTO.setQuantity(1f);

        IngredientDTO second = IngredientDTO.builder()
                .childId(7)
                .netto(netDTO)
                .gross(grossDTO)
                .enable(enableDTO)
                .build();

        return List.of(first, second);
    }

    private ItemDTO getItemDTO(long date) {
        PriceDTO retailPrice = PriceDTO.builder()
                .type(PriceType.RETAIL.toString())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .type(PriceType.DELIVERY.toString())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .name(NEW_ITEM_NAME)
                .printName(NEW_ITEM_NAME)
                .parentId(PARENT_ID)
                .regTime(Instant.now().toEpochMilli())
                .unit(getUnitDTO(Unit.PORTION))
                .workshop(getWorkshopDTO(Workshop.KITCHEN))
                .prices(List.of(retailPrice, deliveryPrice))
                .build();
        return itemDTO;
    }

    private ItemDTO getItemDTOToUpdate(long date) {

        PriceDTO retailPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.RETAIL.toString())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.DELIVERY.toString())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        return ItemDTO.builder()
                .id(NEW_ITEM_ID)
                .name(UPDATE_NAME)
                .printName(UPDATE_NAME)
                .parentId(PARENT_ID)
                .unit(getUnitDTO(Unit.KG))
                .workshop(getWorkshopDTO(Workshop.BAR))
                .prices(List.of(retailPrice, deliveryPrice))
                .build();
    }

    private long convertDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
