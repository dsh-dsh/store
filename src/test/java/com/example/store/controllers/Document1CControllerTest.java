package com.example.store.controllers;

import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.Document;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.services.DocumentService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
@AutoConfigureMockMvc
class Document1CControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;

    private static final String URL_PREFIX = "/api/v1/1с";

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void hold1CDocsTest() throws Exception {
        List<Document> docs = documentService.getAllDocuments();
        docs.forEach(document -> {
            document.setDateTime(LocalDateTime.now().minusDays(1));
            documentRepository.save(document);
        });
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold")
                                .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> documents = documentService.getAllDocuments();
        assertEquals(7, documents.size());
        List<Lot> lots = lotRepository.findAll();
        assertFalse(lots.isEmpty());
        List<LotMovement> lotMovements = lotMoveRepository.findAll();
        assertFalse(lotMovements.isEmpty());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void hold1CDocsThenNoDocsTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold")
                                .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void hold1CDocsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold")
                                .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
