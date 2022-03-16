package com.example.sklad.controllers;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.services.CheckInfoService;
import com.example.sklad.services.DocItemService;
import com.example.sklad.services.DocumentService;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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
public class DocumentControllerTest {

    private static final String URL_PREFIX = "/api/v1/docs";

    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;

    // todo add fields validation tests
    // todo add security tests
    // todo add delete tests

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addCheckDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setCheckDocDTO();
        itemDocDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.CHECK_DOC);
        assertEquals(1, docs.size());

        CheckInfo checkInfo = checkInfoService.getCheckInfo(docs.get(0));
        assertEquals(TestService.CHECK_NUMBER + TestService.ADD_VALUE, checkInfo.getCheckNumber());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addReceiptDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setReceiptDocDTO();
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/receipt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.RECEIPT_DOC);
        assertEquals(1, docs.size());

        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getProject().getId());
        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getStorageTo().getId());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPostingDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setPostingDocDTO();
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/posting")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.POSTING_DOC);
        assertEquals(1, docs.size());

        assertFalse(docs.get(0).isHold());
        assertFalse(docs.get(0).isPayed());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addRequestDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setRequestDocDTO();
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.REQUEST_DOC);
        assertEquals(1, docs.size());

        assertEquals(TestService.AUTHOR_ID, docs.get(0).getAuthor().getId());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateCheckDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setCheckDocDTO();
        testService.addTo(itemDocDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        itemDocDTO.setCheckInfo(testService.setCHeckInfo(TestService.UPDATE_VALUE));
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());

        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(4, items.size());

        CheckInfo checkInfo = checkInfoService.getCheckInfo(doc);
        assertEquals(TestService.CHECK_NUMBER + TestService.UPDATE_VALUE, checkInfo.getCheckNumber());
    }

    @Sql(value = "/sql/documents/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateReceiptDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setReceiptDocDTO();
        testService.addTo(itemDocDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/receipt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());

        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        List<Integer> itemIds = items.stream()
                .map(item -> item.getItem().getId())
                .collect(Collectors.toList());

        assertEquals(4, itemIds.size());
        assertTrue(itemIds.containsAll(TestService.UPDATE_ITEM_IDS));
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updatePostingDocTest() throws Exception {

        ItemDocDTO itemDocDTO = testService.setPostingDocDTO();
        testService.addTo(itemDocDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        itemDocDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        itemDocDTO.setTime(Timestamp.valueOf("2022-01-01 10:30:00"));
        ItemDocRequestDTO requestDTO = testService.setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/posting")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(1, doc.getDateTime().getDayOfMonth());
        assertEquals(10, doc.getDateTime().getHour());
    }



}
