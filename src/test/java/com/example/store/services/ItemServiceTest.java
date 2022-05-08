package com.example.store.services;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ItemServiceTest {

    private static final int ITEM_ID = 4;
    private static final int PARENT_ID = 1;
    private static final int SET_ID = 9;
    private static final int NEW_ITEM_ID = 10;
    private static final String EXISTING_ITEM_NAME = "Картофель фри (1)";
    private static final float RETAIL_PRICE_VALUE = 200.00f;
    private static final float DELIVERY_PRICE_VALUE = 250.00f;
    private static final String NEW_ITEM_NAME = "Новое блюдо";
    private static final String DATE = "2022-02-01";
    private static final String UPDATE_NAME = "Пиво";
    private static final  String UPDATE_DATE = "2022-01-15";

    @Autowired
    private ItemService itemService;

    @Test
    void getItemDTOTreeTest() {
        List<ItemDTOForList> list = itemService.getItemDTOTree();
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNewItemTest() {
        int number = 123456789;
        ItemDTO itemDTO = getItemDTO();
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
        netDTO.setDate(LocalDate.now().toString());
        netDTO.setType(QuantityType.NET.toString());
        netDTO.setQuantity(0.3f);

        QuantityDTO grossDTO = new QuantityDTO();
        grossDTO.setDate(LocalDate.now().toString());
        grossDTO.setType(QuantityType.GROSS.toString());
        grossDTO.setQuantity(0.2f);

        ItemDTO child = new ItemDTO();
        child.setId(8);
        child.setName("Мука");

        IngredientDTO first = IngredientDTO.builder()
                .child(child)
                .quantityList(List.of(netDTO, grossDTO))
                .build();

        netDTO = new QuantityDTO();
        netDTO.setDate(LocalDate.now().toString());
        netDTO.setType(QuantityType.NET.toString());
        netDTO.setQuantity(0.4f);

        grossDTO = new QuantityDTO();
        grossDTO.setDate(LocalDate.now().toString());
        grossDTO.setType(QuantityType.GROSS.toString());
        grossDTO.setQuantity(0.3f);

        child = new ItemDTO();
        child.setId(7);
        child.setName("Картофель фри");

        IngredientDTO second = IngredientDTO.builder()
                .child(child)
                .quantityList(List.of(netDTO, grossDTO))
                .build();

        return List.of(first, second);
    }

    private ItemDTO getItemDTO() {
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
                .type(PriceType.RETAIL.getValue())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .type(PriceType.DELIVERY.getValue())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .name(NEW_ITEM_NAME)
                .printName(NEW_ITEM_NAME)
                .parentId(PARENT_ID)
                .regTime(LocalDateTime.now().toString())
                .unit(Unit.PORTION.toString())
                .workshop(Workshop.KITCHEN.toString())
                .prices(List.of(oldRetailPrice, oldDeliveryPrice, retailPrice, deliveryPrice))
                .build();
        return itemDTO;
    }

    private ItemDTO getItemDTOToUpdate(String date) {

        PriceDTO retailPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.RETAIL.getValue())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.DELIVERY.getValue())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        return ItemDTO.builder()
                .id(NEW_ITEM_ID)
                .name(UPDATE_NAME)
                .printName(UPDATE_NAME)
                .parentId(PARENT_ID)
                .unit(Unit.KG.toString())
                .workshop(Workshop.BAR.toString())
                .prices(List.of(retailPrice, deliveryPrice))
                .build();
    }
}
