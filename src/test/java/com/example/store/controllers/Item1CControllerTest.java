package com.example.store.controllers;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.repositories.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
class Item1CControllerTest extends TestService {

    private static final String URL_PREFIX = "/items";

    @Autowired
    private ItemTestService itemTestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemRepository itemRepository;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setItemsFrom1CTest() throws Exception {

        ItemList1CRequestDTO itemList1CRequestDTO = new ItemList1CRequestDTO();
        itemList1CRequestDTO.setItem1CDTOList(getItemDTOList());

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Item> items = itemRepository.findAll();
        assertEquals(16 , items.size());
    }

    private List<Item1CDTO> getItemDTOList() {
        List<Item1CDTO> list = new ArrayList<>();
        list.add(getItemDTO(11, 2, "Бар", List.of()));
        list.add(getItemDTO(12, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(13, 16, "Ингредиент 2", List.of()));
        list.add(getItemDTO(14, 1, "Блюдо 1",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(444, 1, "Блюдо 10",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(3611, 1, "Cуп лапша (1)",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        180.00f, 220.00f)));
        list.add(getItemDTO(15, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(16, 11, "Ингредиент 2", List.of()));
        return list;
    }

    private Item1CDTO getItemDTO(int number, int parentNumber, String name, List<PriceDTO> prices) {
        Item1CDTO dto = new Item1CDTO();
        dto.setName(name);
        dto.setPrintName(name);
        dto.setRegTime(Instant.now().toEpochMilli());
        dto.setUnit(getUnitDTO(Unit.KG));
        dto.setWorkshop(getWorkshopDTO(Workshop.BAR));
        dto.setPrices(prices);
        dto.setNumber(number);
        dto.setParentNumber(parentNumber);
        return dto;
    }

    private List<PriceDTO> getPrices(long date, float retailValue, float deliveryValue){
        PriceDTO retailPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.RETAIL.toString())
                .value(retailValue)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.DELIVERY.toString())
                .value(deliveryValue)
                .build();
        return List.of(retailPrice, deliveryPrice);
    }

}
