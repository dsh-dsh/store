package com.example.store.controllers;

import com.example.store.ItemTestService;
import com.example.store.model.dto.*;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.services.IngredientService;
import com.example.store.services.ItemService;
import com.example.store.services.SetService;
import com.example.store.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest extends TestService {

    private static final String URL_PREFIX = "/api/v1/items";
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
    private static final  long ITEM_REST_DATE = 1648771200000L; // 2022-01-01


    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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
    @Autowired
    private SetService setService;

    @Test
    void getItemTreeUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemTreeTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].children.[0]").exists())
                .andExpect(jsonPath("$.data.[0].children.[0].children.[1]").exists())
                .andExpect(jsonPath("$.data.[0].children.[0].children.[2]").doesNotExist())
                .andExpect(jsonPath("$.data.[0].children.[1]").doesNotExist())
                .andExpect(jsonPath("$.data.[1].children").isArray())
                .andExpect(jsonPath("$.data.[2]").doesNotExist());

    }

    @Test
    void getItemListUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/list?time=" + ITEM_REST_DATE)
                                .param("id", String.valueOf(NEW_ITEM_ID))
                                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/holdDocsForSerialUnHold.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemListTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/list?time=" + ITEM_REST_DATE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(7))
                .andExpect(jsonPath("$.data.[0].rest_list").isArray())
                .andExpect(jsonPath("$.data.[0].rest_list.[3]").exists())
                .andExpect(jsonPath("$.data.[0].rest_list.[4]").doesNotExist())
                .andExpect(jsonPath("$.data.[0].rest_list.[2].storage.id").value(3))
                .andExpect(jsonPath("$.data.[0].rest_list.[2].quantity").value(2f))
                .andExpect(jsonPath("$.data.[1].id").value(8))
                .andExpect(jsonPath("$.data.[1].rest_list.[0].storage.id").value(1))
                .andExpect(jsonPath("$.data.[1].rest_list.[0].quantity").value(3f))
                .andExpect(jsonPath("$.data.[1].rest_list.[2].storage.id").value(3))
                .andExpect(jsonPath("$.data.[1].rest_list.[2].quantity").value(2f));

    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/holdDocsForSerialUnHold.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemListWhenNoDateTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(7))
                .andExpect(jsonPath("$.data.[0].rest_list").doesNotExist())
                .andExpect(jsonPath("$.data.[1].id").value(8))
                .andExpect(jsonPath("$.data.[1].rest_list").doesNotExist());

    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemTest() throws Exception {
        String date = String.valueOf(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(NEW_ITEM_ID))
                                .param("date", date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(NEW_ITEM_NAME))
                .andExpect(jsonPath("$.data.workshop.code").value(Workshop.KITCHEN.toString()))
                .andExpect(jsonPath("$.data.unit.code").value(Unit.PORTION.toString()))
                .andExpect(jsonPath("$.data.parent_id").value(PARENT_ID))
                .andExpect(jsonPath("$.data.prices.[0].value").value(RETAIL_PRICE_VALUE))
                .andExpect(jsonPath("$.data.sets.[0]").value(SET_ID));

    }

    @Test
    void getItemUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(NEW_ITEM_ID))
                                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = "/sql/hold1CDocs/addIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemCalculationTest() throws Exception{
        String date = String.valueOf(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/calculation")
                                .param("date", date)
                                .param("id", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ingredients").isArray())
                .andExpect(jsonPath("$.data.item_name").isString());

    }

    @Sql(value = "/sql/hold1CDocs/addIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemCalculationWhenItemDoesNotExistsTest() throws Exception{
        String date = String.valueOf(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/calculation")
                                .param("date", date)
                                .param("id", String.valueOf(20)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void getItemCalculationUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/calculation"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setItemWithoutIngredientsAndSetsTest() throws Exception {

        ItemDTO itemDTO = getItemDTO(PRICE_DATE);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
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

        List<Integer> sets = setService.getSets(item);
        assertTrue(sets.isEmpty());

    }

    @Test
    void setItemUnauthorizedTest() throws Exception {

        ItemDTO itemDTO = getItemDTO(PRICE_DATE);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setItemWithIngredientsAndSetsTest() throws Exception {

        ItemDTO itemDTO = getItemDTO(PRICE_DATE);
        itemDTO.setSets(List.of(9));
        itemDTO.setIngredients(getIngredientDTOList());

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertNotNull(item);

        List<Integer> sets = setService.getSets(item);
        assertEquals(List.of(9), sets);

        List<Ingredient> ingredients = ingredientService.getIngredientsNotDeleted(item);
        assertEquals(2, ingredients.size());

    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateItemWithTwoNewPriceTest() throws Exception {

        ItemDTO itemDTO = getItemDTOToUpdate(DATE);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + DATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(UPDATE_NAME, DATE);
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
        assertEquals(6, prices.size());
    }

    @Test
    void updateItemUnauthorizedTest() throws Exception {

        ItemDTO itemDTO = getItemDTOToUpdate(DATE);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + DATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateItemWithOneNewPriceAndNoSetsTest() throws Exception {

        ItemDTO itemDTO = getItemDTOToUpdate(UPDATE_DATE);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + UPDATE_DATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(UPDATE_NAME, UPDATE_DATE);
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

        List<Integer> sets = setService.getSets(item);
        assertTrue(sets.isEmpty());
    }


    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateItemWithIngredientsAndSetsTest() throws Exception {

        ItemDTO itemDTO = getItemDTOToUpdate(DATE);
        itemDTO.setSets(List.of(5, 8));
        itemDTO.setIngredients(getIngredientDTOList());

        this.mockMvc.perform(
                        put(URL_PREFIX + "/" + DATE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(UPDATE_NAME, UPDATE_DATE);
        assertNotNull(item);

        List<Integer> sets = setService.getSets(item);
        assertEquals(List.of(5, 8), sets);

        List<Ingredient> ingredients = ingredientService.getIngredientsNotDeleted(item);
        assertEquals(3, ingredients.size());
    }

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void softDeleteItemTest() throws Exception {
        this.mockMvc.perform(
                        delete(URL_PREFIX + "/" + NEW_ITEM_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Constants.OK));
        Item item = itemTestService.getItemByName(NEW_ITEM_NAME,
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        assertTrue(item.isDeleted());
    }

    @Test
    void softDeleteItemUnauthorized() throws Exception {
        this.mockMvc.perform(
                        delete(URL_PREFIX + "/" + NEW_ITEM_ID))
                .andDo(print())
                .andExpect(status().isUnauthorized());
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

        ItemDTOForIngredient child = new ItemDTOForIngredient();
        child.setId(8);
        child.setName("Мука");

        IngredientDTO first = IngredientDTO.builder()
//                .child(child)
                .childId(8)
//                .quantityList(List.of(netDTO, grossDTO))
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

        child = new ItemDTOForIngredient();
        child.setId(7);
        child.setName("Картофель фри");

        IngredientDTO second = IngredientDTO.builder()
//                .child(child)
                .childId(7)
//                .quantityList(List.of(netDTO, grossDTO))
                .netto(netDTO)
                .gross(grossDTO)
                .enable(enableDTO)
                .build();

        return List.of(first, second);
    }

    private ItemDTO getItemDTO(long date) {
//        PriceDTO oldRetailPrice = PriceDTO.builder()
//                .date(date)
//                .type(PriceType.RETAIL.toString())
//                .value(RETAIL_PRICE_VALUE - 20)
//                .build();
//        PriceDTO oldDeliveryPrice = PriceDTO.builder()
//                .date(date)
//                .type(PriceType.DELIVERY.toString())
//                .value(DELIVERY_PRICE_VALUE - 20)
//                .build();
        PriceDTO retailPrice = PriceDTO.builder()
                .date(date + 8640000L) // + one day
                .type(PriceType.RETAIL.toString())
                .value(RETAIL_PRICE_VALUE)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date + 8640000L)
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
//                .prices(List.of(oldRetailPrice, oldDeliveryPrice, retailPrice, deliveryPrice))
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
