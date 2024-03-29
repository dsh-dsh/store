package com.example.store.controllers;

import com.example.store.components.PeriodDateTime;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.services.*;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentControllerTest {

    private static final String URL_PREFIX = "/api/v1/docs";
    private static final int INDIVIDUAL_ID = 1;
    private static final int STORAGE_ID = 1;
    private static final int SUPPLIER_ID = 1;
    private static final float QUANTITY_FACT = 10.00f;
    private static final long DATE = Util.getLongLocalDateTime("01.06.22 10:30:00");

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
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private PeriodDateTime periodDateTime;

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
                        post(URL_PREFIX + "/dayEnd")
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

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/documents/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addReceiptDocThrowExceptionPeriodTest() throws Exception {

        periodDateTime.setPeriodStart();
        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        docDTO.setDateTime(ZonedDateTime.of(LocalDateTime.parse("2022-01-01T00:00:00.000")
                , ZoneId.systemDefault()).toInstant().toEpochMilli());
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
                        post(URL_PREFIX + "/dayEnd")
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
                        post(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());

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
                        post(URL_PREFIX + "/dayEnd")
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

        LocalDateTime periodEnd = periodDateTime.getEndDateTime();
        periodDateTime.setEndDateTime(LocalDateTime.now().plusMonths(1));

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.REQUEST_DOC);
        assertEquals(1, docs.size());

        assertEquals(TestService.AUTHOR_ID, docs.get(0).getAuthor().getId());

        periodDateTime.setEndDateTime(periodEnd);
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
                        post(URL_PREFIX + "/dayEnd")
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

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void holdDocTest() throws Exception {

        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.POSTING_DOC);
        assertTrue(docs.get(0).isHold());
    }

    @Sql(value = "/sql/documents/addTwoPostingDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void notHoldDocThenExistsNotHoldenDocBeforeTest() throws Exception {

        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/" + 2)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.warning").value(Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void notUnHoldDocThenExistsHoldenDocAfterTest() throws Exception {

        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        post(URL_PREFIX + "/un/hold/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.warning").value(Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void unHoldDocTest() throws Exception {

        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        post(URL_PREFIX + "/un/hold/" + 5)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void holdDocUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addDocsForSerialHold.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void holdSerialDocTest() throws Exception {

        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/serial/" + 6)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<Document> docs = documentService.getAllDocuments();
        assertEquals(6, docs.size());
        assertTrue(docs.get(0).isHold());
        assertTrue(docs.get(5).isHold());
        List<Lot> lots = lotRepository.findAll();
        assertEquals(2, lots.size());
        List<LotMovement> lotMovements = lotMoveRepository.findAll();
        assertEquals(5, lotMovements.size());
    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/addCheckDocAndDocItemsOnly.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void holdSerialDocWhenUnHoldenChecksExistTest() throws Exception {
        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/serial/" + 6)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE));
    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql",
            "/sql/documents/holdDocsForSerialUnHold.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void unHoldSerialDocTest() throws Exception {

        periodDateTime.setStartDateTime(LocalDate.parse("2022-03-01").atStartOfDay());
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/serial/" + 1)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<Document> docs = documentService.getAllDocuments();
        assertEquals(6, docs.size());
        assertFalse(docs.get(0).isHold());
        assertFalse(docs.get(5).isHold());
        List<Lot> lots = lotRepository.findAll();
        assertEquals(0, lots.size());
        List<LotMovement> lotMovements = lotMoveRepository.findAll();
        assertEquals(0, lotMovements.size());
        periodDateTime.setPeriodStart();
    }

    @Sql(value = "/sql/documents/addDocsForSerialHold.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdSerialDocUnauthorizedTest() throws Exception {

        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold/serial/" + 6)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
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
                        put(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = (ItemDoc) documentService.getDocumentById(TestService.DOC_ID);
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
                        put(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = (ItemDoc) documentService.getDocumentById(TestService.DOC_ID);
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
        docDTO.setDateTime(DATE);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        periodDateTime.setPeriodStart();
        this.mockMvc.perform(
                        put(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = (ItemDoc) documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(1, doc.getDateTime().getDayOfMonth());
    }

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateRequestDocTest() throws Exception {

        LocalDateTime periodEnd = periodDateTime.getEndDateTime();
        periodDateTime.setEndDateTime(LocalDateTime.now().plusMonths(1));

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        docDTO.setDateTime(DATE);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = (ItemDoc) documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(DocumentType.REQUEST_DOC, doc.getDocType());
        assertEquals(Month.JUNE, doc.getDateTime().getMonth());

        periodDateTime.setEndDateTime(periodEnd);
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
                        put(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = (ItemDoc) documentService.getDocumentById(TestService.DOC_ID);
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

        LocalDateTime periodEnd = periodDateTime.getEndDateTime();
        periodDateTime.setEndDateTime(LocalDateTime.now().plusMonths(1));

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

        periodDateTime.setEndDateTime(periodEnd);

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
    void FailedTransactionWhenSetNullToDataBaseNotNullFieldTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));
        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        docDTO.getCheckInfo().setCashRegisterNumber(null);
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/dayEnd")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPostingDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("filter","posting")
                        .param("start","1646082000000")
                        .param("end","1648760400000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(3));
    }

    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCreditOrderDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("filter","order")
                        .param("start","1646082000000")
                        .param("end","1648760400000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(6));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCheckDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("filter","check")
                        .param("start","1646082000000")
                        .param("end","1648760400000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("filter","default")
                        .param("start","1646082000000")
                        .param("end","1648760400000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1))
                .andExpect(jsonPath("$.data.[1].id").value(2))
                .andExpect(jsonPath("$.data.[2].id").value(3))
                .andExpect(jsonPath("$.data.[3].id").value(4))
                .andExpect(jsonPath("$.data.[4].id").value(5));
    }

    @Sql(value = "/sql/documents/addTenChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocListPeriodTest() throws Exception {
        String date = String.valueOf(Util.getLongLocalDate(LocalDate.parse("2022-04-15")));
        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("filter", "default")
                        .param("start",date)
                        .param("end",date))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1))
                .andExpect(jsonPath("$.data.[1].id").value(2))
                .andExpect(jsonPath("$.data.[2].id").value(3))
                .andExpect(jsonPath("$.data.[3].id").value(4))
                .andExpect(jsonPath("$.data.[4].id").value(5))
                .andExpect(jsonPath("$.data.[5]").doesNotExist());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocListUnauthorizedTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list")
                        .param("start","1646082000000")
                        .param("end","1648760400000"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCheckDocTest() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX)
                        .param("id", String.valueOf(TestService.DOC_ID))
                        .param("copy", String.valueOf(false)))
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
                        .param("id", String.valueOf(TestService.DOC_ID))
                        .param("copy", String.valueOf(false)))
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

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getMoveDocFromRequestTest() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX + "/move/from/request")
                        .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(0))
                .andExpect(jsonPath("$.data.check_info").doesNotExist())
                .andExpect(jsonPath("$.data.doc_items").isArray())
                .andExpect(jsonPath("$.data.doc_items.[1]").exists())
                .andExpect(jsonPath("$.data.doc_items.[2]").doesNotExist())
                .andExpect(jsonPath("$.data.doc_items.[0].quantity").value(0))
                .andExpect(jsonPath("$.data.doc_items.[0].quantity_fact").value(1))
                .andExpect(jsonPath("$.data.doc_items.[1].quantity").value(0))
                .andExpect(jsonPath("$.data.doc_items.[1].quantity_fact").value(2));
    }

    @Test
    void getMoveDocFromRequestUnauthorized() throws Exception {
        this.mockMvc.perform(get(URL_PREFIX + "/move/from/request")
                        .param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    @Sql(value = {"/sql/documents/add5DocList.sql",
            "/sql/documents/addOrderDoc.sql",
            "/sql/documents/softDelete.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void hardDeleteDocumentsTest() throws Exception {
        this.mockMvc.perform(
                        delete(URL_PREFIX + "/hard/delete"))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> docs = documentService.getAllDocuments();
        assertEquals(3, docs.size());
    }

    @Test
    void hardDeleteDocumentsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        delete(URL_PREFIX + "/hard/delete"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewDocNumberWhenRequestDocUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/new/number?type=" + DocumentType.REQUEST_DOC.getValue()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getNewDocNumberWhenRequestDocTest() throws Exception {
        documentRepository.updateDateTimeOfDocForTestsOnly(DocumentType.REQUEST_DOC.toString(), LocalDate.now().atStartOfDay());
        this.mockMvc.perform(
                        get(URL_PREFIX + "/new/number?type=" + DocumentType.REQUEST_DOC.getValue()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(12));
    }

    @Test
    void addPaymentUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    void addPaymentTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        List<Document> documents = documentRepository.findAll();
        assertEquals(2, documents.size());
        ItemDoc postingDoc = (ItemDoc) documents.get(0);
        OrderDoc orderDoc = (OrderDoc) documents.get(1);
        assertTrue(postingDoc.isPayed());
        assertEquals(orderDoc, postingDoc.getBaseDocument());
        assertEquals(orderDoc.getRecipient(), postingDoc.getSupplier());
        assertEquals(orderDoc.getAmount(), docItemService.getItemsAmount(postingDoc));
    }

    @Test
    void addSupplierPaymentsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/payments/ООО \"Защита\"")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addThreePostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    void addSupplierPaymentsTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/payments/ООО \"Защита\"")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        List<Document> documents = documentRepository.findAll();
        assertEquals(4, documents.size());
        ItemDoc postingDoc = (ItemDoc) documents.get(2);
        OrderDoc orderDoc = (OrderDoc) documents.get(3);
        assertTrue(postingDoc.isPayed());
        assertEquals(orderDoc, postingDoc.getBaseDocument());
        assertEquals(orderDoc.getRecipient(), postingDoc.getSupplier());
        float amount = (float) documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.POSTING_DOC)
                .mapToDouble(doc -> docItemService.getItemsAmount((ItemDoc) doc))
                .sum();
        assertEquals(orderDoc.getAmount(), amount);
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    void addPaymentWhenNoPostingDocThenBadRequestTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deletePaymentUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/delete/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = {"/sql/documents/addPostingDoc.sql", "/sql/documents/addPaymentDoc.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    void deletePaymentTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/delete/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> documents = documentRepository.findAll();
        assertEquals(2, documents.size());
        ItemDoc postingDoc = (ItemDoc) documents.get(0);
        OrderDoc orderDoc = (OrderDoc) documents.get(1);
        assertFalse(postingDoc.isPayed());
        assertNull(postingDoc.getBaseDocument());
        assertTrue(orderDoc.isDeleted());
        assertNull(orderDoc.getBaseDocument());
    }

    @Sql(value = {"/sql/documents/addPostingDoc.sql", "/sql/documents/addPaymentDoc.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    @Transactional
    void deletePaymentWhenPaymentDocIsHoldTest() throws Exception {
        Document document = documentRepository.getById(2);
        document.setHold(true);
        documentRepository.save(document);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/delete/payment/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void getDocsToPayUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/to/pay")
                                .param("companyId", "2"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/addThreePostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithUserDetails(TestService.EXISTING_EMAIL)
    @Test
    void getDocsToPayTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/to/pay")
                                .param("companyId", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1))
                .andExpect(jsonPath("$.data.[1].id").value(2))
                .andExpect(jsonPath("$.data.[2].id").value(3));
    }
}
