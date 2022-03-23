package com.example.sklad.controllers;

import com.example.sklad.model.dto.DocItemDTO;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.dto.requests.DocRequestDTO;
import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.services.CheckInfoService;
import com.example.sklad.services.DocItemService;
import com.example.sklad.services.DocumentService;
import com.example.sklad.utils.Constants;
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
public class DocumentControllerTest {

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
    // todo add security tests
    // todo add delete tests


    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addCheckDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);

        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));

        docDTO.setCheckInfo(testService.setCHeckInfo(TestService.ADD_VALUE));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/check")
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

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addReceiptDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        docDTO.setSupplier(testService.setCompanyDTO(2));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/receipt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.RECEIPT_DOC);
        assertEquals(1, docs.size());

        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getProject().getId());
        assertEquals(TestService.RECEIPT_FIELDS_ID, docs.get(0).getStorageTo().getId());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPostingDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.POSTING_DOC);
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/posting")
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
    void addRequestDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/request")
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
    void addInventoryDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);

        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        docDTO.setStorageFrom(testService.setStorageDTO(STORAGE_ID));

        docDTO.setDocItems(testService.setDocItemDTOList(TestService.ADD_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/inventory")
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

        DocDTO docDTO = testService.setDTOFields(DocumentType.RECEIPT_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setSupplier(testService.setCompanyDTO(2));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(TestService.RECEIPT_FIELDS_ID));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

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

        DocDTO docDTO = testService.setDTOFields(DocumentType.POSTING_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setRecipient(testService.setCompanyDTO(1));
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
//        docDTO.setTime(Timestamp.valueOf("2022-01-01 10:30:00"));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/posting")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
//        assertEquals(1, doc.getDateTime().getDayOfMonth());
//        assertEquals(10, doc.getDateTime().getHour());
    }

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateRequestDocTest() throws Exception {

        DocDTO docDTO = testService.setDTOFields(DocumentType.REQUEST_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setStorageTo(testService.setStorageDTO(1));
        docDTO.setDocItems(testService.setDocItemDTOList(TestService.UPDATE_VALUE));
//        docDTO.setTime(Timestamp.valueOf("2022-02-01 10:30:00"));
        DocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(TestService.DOC_ID);
        assertEquals(TestService.DOC_NUMBER, doc.getNumber());
        assertEquals(DocumentType.REQUEST_DOC, doc.getDocType());
//        assertEquals(Month.FEBRUARY, doc.getDateTime().getMonth());
    }

    @Sql(value = "/sql/documents/addInventoryDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
                        put(URL_PREFIX + "/inventory")
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
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

        int count = docItemService.countItemsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

        count = checkInfoService.countRowsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

        int count = docItemService.countItemsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

    }

    @Sql(value = "/sql/documents/addRequestDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

        int count = docItemService.countItemsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

    }

    @Sql(value = "/sql/documents/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

        int count = docItemService.countItemsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

    }

    @Sql(value = "/sql/documents/addInventoryDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
        assertEquals(TestService.NO_DOCUMENTS, docs.size());

        int count = docItemService.countItemsByDoc(TestService.DOC_ID);
        assertEquals(TestService.NO_DOCUMENTS, count);

    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
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
                        post(URL_PREFIX + "/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.TRANSACTION_FAILED_MESSAGE));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocListTest() throws Exception {

        this.mockMvc.perform(get(URL_PREFIX + "/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id").value(1))
                .andExpect(jsonPath("$.data.[1].id").value(2))
                .andExpect(jsonPath("$.data.[2].id").value(3))
                .andExpect(jsonPath("$.data.[3].id").value(4))
                .andExpect(jsonPath("$.data.[4].id").value(5));
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCheckTest() throws Exception {

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
    void getPostingTest() throws Exception {

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
}
