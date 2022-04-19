package com.example.store.controllers;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import com.example.store.services.DocumentService;
import com.example.store.utils.Constants;
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

import java.time.Month;
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
class DocumentControllerTest {

    private static final String URL_PREFIX = "/api/v1/docs";
    private static final int INDIVIDUAL_ID = 1;
    private static final int STORAGE_ID = 1;
    private static final int SUPPLIER_ID = 1;
    private static final float QUANTITY_FACT = 10.00f;

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


    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addCheckDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);

        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));

        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        assertEquals(1, docs.size());

        CheckInfo checkInfo = checkInfoService.getCheckInfo(docs.get(0));
        assertEquals(TestService.CHECK_NUMBER + TestService.ADD_VALUE, checkInfo.getCheckNumber());
    }

    @Test
    void addCheckDocUnauthorizedTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addReceiptDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        docDTO.setSupplier(testService.setCompanyDTO(2));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.RECEIPT_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());

        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getProject().getId());
        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getStorageTo().getId());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addReceiptDocWithWrongFirstDocItemTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        docDTO.setSupplier(testService.setCompanyDTO(2));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        docDTO.getDocItems().get(0).setItemId(1000);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.TRANSACTION_FAILED_MESSAGE));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.RECEIPT_DOC);
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addPostingDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.POSTING_DOC);
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.POSTING_DOC);
        assertEquals(1, docs.size());

        assertFalse(docs.get(0).isHold());
        assertFalse(docs.get(0).isPayed());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addRequestDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.REQUEST_DOC);
        assertEquals(1, docs.size());

        assertEquals(TestService.AUTHOR_ID, docs.get(0).getAuthor().getId());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addInventoryDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);

        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        docDTO.setStorageFrom(testService.setStorageDTO(STORAGE_ID));

        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.INVENTORY_DOC);
        assertEquals(1, docs.size());

        assertEquals(INDIVIDUAL_ID, docs.get(0).getIndividual().getId());
        assertEquals(SUPPLIER_ID, docs.get(0).getSupplier().getId());
        assertEquals(STORAGE_ID, docs.get(0).getStorageFrom().getId());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateCheckDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));
        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.UPDATE_VALUE));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
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

    @Test
    void updateCheckDocUnauthorizedTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateReceiptDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setSupplier(testService.setCompanyDTO(2));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
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
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updatePostingDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.POSTING_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        docDTO.setTime("2022-01-01T10:30:00");
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
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

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateRequestDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        docDTO.setTime("2022-02-01T10:30:00");
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(DocumentType.REQUEST_DOC, doc.getDocType());
        assertEquals(Month.FEBRUARY, doc.getDateTime().getMonth());
    }

    @Sql(value = "/sql/documents/addInventoryDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateInventoryDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        docDTO.setStorageFrom(testService.setStorageDTO(STORAGE_ID));
        List<DocItemDTO> itemDTOList = testService.setDocItemDTOList(TestService.UPDATE_VALUE);
        itemDTOList.forEach(dto -> dto.setQuantityFact(10.00f));
        docDTO.setDocItems(itemDTOList);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(DocumentType.INVENTORY_DOC, doc.getDocType());

        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(QUANTITY_FACT, items.get(0).getQuantityFact());

    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void deleteCheckDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());

        int count = checkInfoService.countRowsByDoc(TestService.DOC_ID);
        assertEquals(TestService.ONE_DOCUMENT, count);

    }


    @Test
    void deleteCheckDocUnauthorizedTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void deletePostingDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.POSTING_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.POSTING_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());

    }

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void deleteRequestDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.REQUEST_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());

    }

    @Sql(value = "/sql/documents/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void deleteReceiptDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.RECEIPT_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());

    }

    @Sql(value = "/sql/documents/addInventoryDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void deleteInventoryDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        List<DocItemDTO> itemDTOList = testService.setDocItemDTOList(TestService.UPDATE_VALUE);
        docDTO.setDocItems(itemDTOList);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.INVENTORY_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());

    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void FailedTransactionTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));
        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        docDTO.getCheckInfo().setCashRegisterNumber(null);
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.TRANSACTION_FAILED_MESSAGE));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("page", "0")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1))
                .andExpect(jsonPath("$.data.[1].id").value(2))
                .andExpect(jsonPath("$.data.[2].id").value(3))
                .andExpect(jsonPath("$.data.[3].id").value(4))
                .andExpect(jsonPath("$.data.[4].id").value(5));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocListPaginationTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("page", "1")
                        .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(3))
                .andExpect(jsonPath("$.data.[1].id").value(4))
                .andExpect(jsonPath("$.data.[2].id").doesNotExist());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCheckDocTest() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX)
                        .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.check_info").exists())
                .andExpect(jsonPath("$.data.doc_items").isArray())
                .andExpect(jsonPath("$.data.doc_items.[2]").exists())
                .andExpect(jsonPath("$.data.doc_items.[3]").doesNotExist());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPostingDocTest() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX)
                        .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.check_info").doesNotExist())
                .andExpect(jsonPath("$.data.doc_items").isArray())
                .andExpect(jsonPath("$.data.doc_items.[1]").exists())
                .andExpect(jsonPath("$.data.doc_items.[2]").doesNotExist());
    }

    @Test
    void getPostingDocTestUnauthorized() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX)
                        .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
