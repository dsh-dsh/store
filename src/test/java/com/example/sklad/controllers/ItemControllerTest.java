package com.example.sklad.controllers;

import com.example.sklad.model.enums.Unit;
import com.example.sklad.model.enums.Workshop;
import com.example.sklad.services.UserService;
import com.example.sklad.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private static final float PRICE_VALUE = 120.00f;
    private static final String NEW_ITEM_NAME = "Новое блюдо (1)";

    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService itemService;

    @Test
    void getItem() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(ITEM_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(EXISTING_ITEM_NAME))
                .andExpect(jsonPath("$.data.workshop").value(Workshop.KITCHEN.toString()))
                .andExpect(jsonPath("$.data.unit").value(Unit.PORTION.toString()))
                .andExpect(jsonPath("$.data.parent_id").value(PARENT_ID))
                .andExpect(jsonPath("$.data.prices.[0].value").value(PRICE_VALUE));

    }
}
