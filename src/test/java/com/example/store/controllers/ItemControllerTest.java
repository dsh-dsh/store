package com.example.store.controllers;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.QuantityType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.services.IngredientService;
import com.example.store.services.ItemService;
import com.example.store.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    private static final String URL_PREFIX = "/api/v1/items";
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

    @Autowired
    private ItemTestService itemTestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientService ingredientService;

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItem() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(NEW_ITEM_ID))
                                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(NEW_ITEM_NAME))
                .andExpect(jsonPath("$.data.workshop").value(Workshop.KITCHEN.toString()))
                .andExpect(jsonPath("$.data.unit").value(Unit.PORTION.toString()))
                .andExpect(jsonPath("$.data.parent_id").value(PARENT_ID))
                .andExpect(jsonPath("$.data.prices.[0].value").value(RETAIL_PRICE_VALUE))
                .andExpect(jsonPath("$.data.sets.[0]").value(SET_ID));

    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setItemWithoutIngredientsAndSets() throws Exception {

        ItemDTO itemDTO = getItemDTO();

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME, LocalDate.now());
        assertNotNull(item);
        assertEquals(NEW_ITEM_NAME, item.getName());
        assertEquals(Unit.PORTION, item.getUnit());
        assertEquals(Workshop.KITCHEN, item.getWorkshop());
        assertEquals(PARENT_ID, item.getParent().getId());
        assertEquals(2, item.getPrices().size());
        assertEquals(RETAIL_PRICE_VALUE, item.getPrices().get(0).getValue());
        assertEquals(PriceType.RETAIL, item.getPrices().get(0).getPriceType());
        assertEquals(DELIVERY_PRICE_VALUE, item.getPrices().get(1).getValue());
        assertEquals(PriceType.DELIVERY, item.getPrices().get(1).getPriceType());

    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setItemWithIngredientsAndSets() throws Exception {

        ItemDTO itemDTO = getItemDTO();
        itemDTO.setSets(List.of(9));
        itemDTO.setIngredients(getIngredientDTOList());

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME, LocalDate.now());
        assertNotNull(item);

        List<Ingredient> ingredients = ingredientService.getIngredients(item);
        assertEquals(2, ingredients.size());

    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateItemWithTwoNewPrice() throws Exception {

        PriceDTO retailPrice = PriceDTO.builder()
                .date(DATE)
                .type(PriceType.RETAIL.getType())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(DATE)
                .type(PriceType.DELIVERY.getType())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .id(NEW_ITEM_ID)
                .name(UPDATE_NAME)
                .printName(UPDATE_NAME)
                .parentId(PARENT_ID)
                .unit(Unit.KG.toString())
                .workshop(Workshop.BAR.toString())
                .prices(List.of(retailPrice, deliveryPrice))
                .build();

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + DATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(UPDATE_NAME, LocalDate.parse(DATE));
        assertNotNull(item);
        assertEquals(UPDATE_NAME, item.getName());
        assertEquals(Unit.KG, item.getUnit());
        assertEquals(Workshop.BAR, item.getWorkshop());
        assertEquals(PARENT_ID, item.getParent().getId());
        assertEquals(2, item.getPrices().size());
        assertEquals(RETAIL_PRICE_VALUE, item.getPrices().get(0).getValue());
        assertEquals(PriceType.RETAIL, item.getPrices().get(0).getPriceType());
        assertEquals(DELIVERY_PRICE_VALUE, item.getPrices().get(1).getValue());
        assertEquals(PriceType.DELIVERY, item.getPrices().get(1).getPriceType());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateItemWithOneNewPrice() throws Exception {

        String updateDate = "2022-01-15";

        PriceDTO retailPrice = PriceDTO.builder()
                .date(updateDate)
                .type(PriceType.RETAIL.getType())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(updateDate)
                .type(PriceType.DELIVERY.getType())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .id(NEW_ITEM_ID)
                .name(UPDATE_NAME)
                .printName(UPDATE_NAME)
                .parentId(PARENT_ID)
                .unit(Unit.KG.toString())
                .workshop(Workshop.BAR.toString())
                .prices(List.of(retailPrice, deliveryPrice))
                .build();

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + updateDate)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(UPDATE_NAME, LocalDate.parse(updateDate));
        assertNotNull(item);
        assertEquals(UPDATE_NAME, item.getName());
        assertEquals(Unit.KG, item.getUnit());
        assertEquals(Workshop.BAR, item.getWorkshop());
        assertEquals(PARENT_ID, item.getParent().getId());
        assertEquals(2, item.getPrices().size());
        assertEquals(RETAIL_PRICE_VALUE, item.getPrices().get(0).getValue());
        assertEquals(PriceType.RETAIL, item.getPrices().get(0).getPriceType());
        assertEquals(DELIVERY_PRICE_VALUE, item.getPrices().get(1).getValue());
        assertEquals(PriceType.DELIVERY, item.getPrices().get(1).getPriceType());

        List<Price> prices = itemTestService.getItemPriceList(item);
        assertEquals(5, prices.size());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteItem() throws Exception {
        this.mockMvc.perform(
                        delete(URL_PREFIX + "/" + NEW_ITEM_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Constants.OK));

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME, LocalDate.now());
        assertTrue(item.isDeleted());
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
                .type(PriceType.RETAIL.getType())
                .value(RETAIL_PRICE_VALUE - 20)
                .build();
        PriceDTO oldDeliveryPrice = PriceDTO.builder()
                .date("2022-01-01")
                .type(PriceType.DELIVERY.getType())
                .value(DELIVERY_PRICE_VALUE - 20)
                .build();
        PriceDTO retailPrice = PriceDTO.builder()
                .type(PriceType.RETAIL.getType())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .type(PriceType.DELIVERY.getType())
                .value(DELIVERY_PRICE_VALUE)
                .build();

        ItemDTO itemDTO = ItemDTO.builder()
                .name(NEW_ITEM_NAME)
                .printName(NEW_ITEM_NAME)
                .parentId(PARENT_ID)
                .regTime(itemTestService.dateTimeToLong(LocalDateTime.now().toString()))
                .unit(Unit.PORTION.toString())
                .workshop(Workshop.KITCHEN.toString())
                .prices(List.of(oldRetailPrice, oldDeliveryPrice, retailPrice, deliveryPrice))
                .build();
        return itemDTO;
    }

}
