package com.example.store.services;

import com.example.store.controllers.TestService;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.*;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    private IngredientService ingredientService;

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
    @Transactional
    void updateItemTest() {
        ItemDTO itemDTO = getItemDTOToUpdate(UPDATE_DATE);
        itemDTO.setId(10);
        itemDTO.setSets(List.of(9));
        itemDTO.setIngredients(getIngredientDTOList());
        itemService.updateItem(itemDTO, UPDATE_DATE);
        Item item = itemService.getItemById(10);
        assertEquals(Workshop.BAR, item.getWorkshop());
        assertEquals(Unit.KG, item.getUnit());
        assertEquals(5, item.getPrices().size());
        List<Ingredient> ingredients = ingredientService.getIngredientsNotDeleted(item);
        assertEquals(3, ingredients.size());
        // todo в тесте не добавляется quantities в третьем ингредиенте
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void updateItemFieldsTest() {
        ItemDTO itemDTO = getItemDTOToUpdate(UPDATE_DATE);
        Item item = itemService.getItemById(10);
        itemService.updateItemFields(item, itemDTO);
        assertEquals(UPDATE_NAME, item.getName());
        assertEquals(Workshop.BAR, item.getWorkshop());
        assertEquals(Unit.KG, item.getUnit());
    }

    @Test
    void getItemDTOListTest() {
        List<ItemDTOForList> list = itemService.getItemDTOList();
        assertNotNull(list);
        assertEquals(7, list.get(0).getId());
        assertEquals(4, list.get(0).getRestList().size());
    }

    @Transactional
    @Test
    void getItemByIdTest() {
        Item item = itemService.getItemById(2);
        assertEquals("Ингридиенты", item.getName());
    }

    @Test
    void findItemByNumberTest() {
        Optional<Item> item = itemService.findItemByNumber(3611);
        assertTrue(item.isPresent());
        assertEquals("Cуп лапша (1)", item.get().getName());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDTOByIdTest() {
        long date = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        ItemDTO dto = itemService.getItemDTOById(10, date);
        assertNotNull(dto);
        assertEquals(10, dto.getId());
        assertEquals(444, dto.getNumber());
        assertEquals(2, dto.getIngredients().size());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void softDeleteItemTest() {
        itemService.softDeleteItem(10);
        Item item = itemService.getItemById(10);
        assertTrue(item.isDeleted());
        List<Ingredient> ingredients = ingredientService.getIngredientsNotDeleted(item);
        assertEquals(0, ingredients.size());
    }

    @Test
    void findItemByIdTest() {
        Item item = itemService.findItemById(9);
        assertNotNull(item);
    }

    @Test
    void findItemByIdIfNotExistsTest() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.findItemById(90));
        assertEquals(Constants.NO_SUCH_ITEM_MESSAGE, exception.getMessage());
    }

    @Test
    void getParentTest() {
        Item child = itemService.getItemById(9);
        Item parent = itemService.getParent(child);
        assertEquals(1, parent.getId());
    }

    @Test
    void ThrowsExceptionGetParentTest() {
        Item child = itemService.getItemById(1);
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.getParent(child));
        assertEquals(Constants.NO_SUCH_ITEM_MESSAGE, exception.getMessage());
    }


    private List<IngredientDTO> getIngredientDTOList() {
        PeriodicValueDTO netDTO = new PeriodicValueDTO();
        netDTO.setDate(convertDate(LocalDate.now()));
        netDTO.setType(PeriodicValueType.NET.toString());
        netDTO.setQuantity(0.3f);

        PeriodicValueDTO grossDTO = new PeriodicValueDTO();
        grossDTO.setDate(convertDate(LocalDate.now()));
        grossDTO.setType(PeriodicValueType.GROSS.toString());
        grossDTO.setQuantity(0.2f);

        PeriodicValueDTO enableDTO = new PeriodicValueDTO();
        enableDTO.setDate(convertDate(LocalDate.now()));
        enableDTO.setType(PeriodicValueType.ENABLE.toString());
        enableDTO.setQuantity(1f);

        IngredientDTO first = IngredientDTO.builder()
                .childId(8)
                .netto(netDTO)
                .gross(grossDTO)
                .enable(enableDTO)
                .build();

        netDTO = new PeriodicValueDTO();
        netDTO.setDate(convertDate(LocalDate.now()));
        netDTO.setType(PeriodicValueType.NET.toString());
        netDTO.setQuantity(0.4f);

        grossDTO = new PeriodicValueDTO();
        grossDTO.setDate(convertDate(LocalDate.now()));
        grossDTO.setType(PeriodicValueType.GROSS.toString());
        grossDTO.setQuantity(0.3f);

        enableDTO = new PeriodicValueDTO();
        enableDTO.setDate(convertDate(LocalDate.now()));
        enableDTO.setType(PeriodicValueType.ENABLE.toString());
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

        return ItemDTO.builder()
                .name(NEW_ITEM_NAME)
                .printName(NEW_ITEM_NAME)
                .parentId(PARENT_ID)
                .regTime(Instant.now().toEpochMilli())
                .unit(getUnitDTO(Unit.PORTION))
                .workshop(getWorkshopDTO(Workshop.KITCHEN))
                .prices(List.of(retailPrice, deliveryPrice))
                .build();
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
