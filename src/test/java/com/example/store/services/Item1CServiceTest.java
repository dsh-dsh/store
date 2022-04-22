package com.example.store.services;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class Item1CServiceTest {

    @Autowired
    private Item1CService item1CService;
    @Autowired
    private ItemService itemService;

    private static final int NUMBER = 444;
    private static final int PARENT_NUMBER = 3;
    private static final int SET_ID = 9;
    private static final int NEW_ITEM_ID = 10;
    private static final String EXISTING_ITEM_NAME = "Картофель фри (1)";
    private static final float RETAIL_PRICE_VALUE = 200.00f;
    private static final float DELIVERY_PRICE_VALUE = 250.00f;
    private static final String NEW_ITEM_NAME = "Новое блюдо";
    private static final String DATE = "2022-02-01";
    private static final String UPDATE_NAME = "Пиво";
    private static final  String UPDATE_DATE = "2022-01-15";

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setItemWhenNotExistsThenSetTest() {
        Item1CDTO dto = getItemDTO();
        item1CService.setItem(dto);
        Optional<Item> itemOptional = itemService.findItemByNumber(dto.getNumber());
        assertTrue(itemOptional.isPresent());
        assertEquals(NEW_ITEM_NAME, itemOptional.get().getName());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
//    @Transactional
    void setItemWhenExistsThenUpdateTest() {
        Item1CDTO dto = getItemDTO();
        dto.getPrices().get(1).setValue(500.00f);
        item1CService.setDate(LocalDate.now());
        item1CService.setItem(dto);
        Optional<Item> itemOptional = itemService.findItemByNumber(dto.getNumber());
        assertTrue(itemOptional.isPresent());
        assertEquals(NEW_ITEM_NAME, itemOptional.get().getName());
//        assertEquals(5, itemOptional.get().getPrices().size());
    }

//    @Test
//    void setNewItemTest() {
//    }
//
//    @Test
//    void updateItemTest() {
//    }

    Item1CDTO getItemDTO() {

        PriceDTO oldRetailPrice = PriceDTO.builder()
                .date("2022-01-01")
                .type(PriceType.RETAIL.getValue())
                .value(RETAIL_PRICE_VALUE - 20)
                .build();
        PriceDTO oldDeliveryPrice = PriceDTO.builder()
                .date("2022-01-01")
                .type(PriceType.DELIVERY.getValue())
                .value(DELIVERY_PRICE_VALUE - 20)
                .build();

        PriceDTO retailPrice = PriceDTO.builder()
                .date("2022-04-01")
                .type(PriceType.RETAIL.getValue())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date("2022-04-01")
                .type(PriceType.DELIVERY.getValue())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        Item1CDTO dto = new Item1CDTO();
        dto.setName(NEW_ITEM_NAME);
        dto.setPrintName(NEW_ITEM_NAME);
        dto.setRegTime(LocalDateTime.now().toString());
        dto.setUnit(Unit.PORTION.toString());
        dto.setWorkshop(Workshop.KITCHEN.toString());
        dto.setPrices(List.of(retailPrice, deliveryPrice));
        dto.setNumber(NUMBER);
        dto.setParentNumber(PARENT_NUMBER);

        return dto;
    }
}