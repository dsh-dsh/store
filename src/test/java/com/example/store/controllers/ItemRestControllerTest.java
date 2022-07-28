package com.example.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
class ItemRestControllerTest {

    private static final String URL_PREFIX = "/api/v1/rest";
    private static final String DOC_ID = "7";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;


    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/holdDocsForSerialUnHold.sql",
            "/sql/rest/addInventoryDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/rest/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemRestWithStorage3Test()  throws Exception {
        String time = String.valueOf(ZonedDateTime.of(LocalDateTime.now(),
                ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/inventory")
                                .param("docId", DOC_ID)
                                .param("time", time)
                                .param("storageId", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[0].item_id").value(7))
                .andExpect(jsonPath("$.data.[0].document_id").value(DOC_ID))
                .andExpect(jsonPath("$.data.[0].quantity").value(2f))
                .andExpect(jsonPath("$.data.[0].price").value(200f))
                .andExpect(jsonPath("$.data.[1].item_id").value(8))
                .andExpect(jsonPath("$.data.[1].document_id").value(DOC_ID))
                .andExpect(jsonPath("$.data.[1].quantity").value(2f))
                .andExpect(jsonPath("$.data.[1].price").value(100f));
    }
    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/holdDocsForSerialUnHold.sql",
            "/sql/rest/addInventoryDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/rest/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemRestWithStorage1Test()  throws Exception {
        String time = String.valueOf(ZonedDateTime.of(LocalDateTime.now(),
                ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/inventory")
                                .param("docId", DOC_ID)
                                .param("time", time)
                                .param("storageId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[0].item_id").value(7))
                .andExpect(jsonPath("$.data.[0].document_id").value(DOC_ID))
                .andExpect(jsonPath("$.data.[0].quantity").value(0))
                .andExpect(jsonPath("$.data.[0].price").value(200f))
                .andExpect(jsonPath("$.data.[1].item_id").value(8))
                .andExpect(jsonPath("$.data.[1].document_id").value(DOC_ID))
                .andExpect(jsonPath("$.data.[1].quantity").value(3f))
                .andExpect(jsonPath("$.data.[1].price").value(100f));
    }

    @Sql(value = "/sql/rest/addInventoryDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/rest/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemRestUnauthorizedTest()  throws Exception {
        String time = String.valueOf(ZonedDateTime.of(LocalDateTime.now(),
                                ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/inventory")
                                .param("docId", DOC_ID)
                                .param("time", time)
                                .param("storageId", "3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}