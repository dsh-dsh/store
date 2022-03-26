package com.example.store.controllers;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.services.ItemService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private static final String EXISTING_ITEM_NAME = "Картофель фри (1)";
    private static final float RETAIL_PRICE_VALUE = 120.00f;
    private static final float DELIVERY_PRICE_VALUE = 150.00f;
    private static final String NEW_ITEM_NAME = "Новое блюдо (1)";

    @Autowired
    private ItemTestService itemTestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemService itemService;

    @Test
    void getItem() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(ITEM_ID))
                                .param("date", LocalDate.now().toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(EXISTING_ITEM_NAME))
                .andExpect(jsonPath("$.data.workshop").value(Workshop.KITCHEN.toString()))
                .andExpect(jsonPath("$.data.unit").value(Unit.PORTION.toString()))
                .andExpect(jsonPath("$.data.parent_id").value(PARENT_ID))
                .andExpect(jsonPath("$.data.prices.[0].value").value(RETAIL_PRICE_VALUE));

    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setItem() throws Exception {

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
                .name("Новое блюдо (1)")
                .printName("Новое блюдо")
                .parentId(PARENT_ID)
                .regTime(itemTestService.dateTimeToLong(LocalDateTime.now().toString()))
                .unit(Unit.PORTION.toString())
                .workshop(Workshop.KITCHEN.toString())
                .prices(List.of(oldRetailPrice, oldDeliveryPrice, retailPrice, deliveryPrice))
                .build();

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        Item item = itemTestService.getItemByName(NEW_ITEM_NAME);
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

}
