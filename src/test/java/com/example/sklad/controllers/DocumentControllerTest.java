package com.example.sklad.controllers;

import com.example.sklad.model.dto.*;
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
import java.time.LocalDateTime;
import java.util.Calendar;
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

    private static final String URL_PREFIX = "/api/v1/docs";
    private static final int ADD_VALUE = 0;
    private static final int UPDATE_VALUE = 1;
    private static final int DOC_ID = 1;
    private static final int DOC_NUMBER = 11111;
    private static final int CHECK_NUMBER = 654321;
    private static final int RECEIPT_FIELDS_ID = 1;
    private static final int AUTHOR_ID = 1;
    private static final List<Integer> ADDED_ITEM_IDS = List.of(1, 2, 3, 4);
    private static final List<Integer> UPDATE_ITEM_IDS = List.of(2, 3, 4, 5);

    // todo add fields validation tests
    // todo add security tests
    // todo add delete tests

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addCheckDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setCheckDocDTO();
        itemDocDTO.setCheckInfo(setCHeckInfo(ADD_VALUE));
        itemDocDTO.setDocItems(setDocItemDTOList(ADD_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

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
        assertEquals(CHECK_NUMBER + ADD_VALUE, checkInfo.getCheckNumber());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addReceiptDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setReceiptDocDTO();
        itemDocDTO.setDocItems(setDocItemDTOList(ADD_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/receipt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.RECEIPT_DOC);
        assertEquals(1, docs.size());

        assertEquals(RECEIPT_FIELDS_ID, docs.get(0).getProject().getId());
        assertEquals(RECEIPT_FIELDS_ID, docs.get(0).getStorageTo().getId());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPostingDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setPostingDocDTO();
        itemDocDTO.setDocItems(setDocItemDTOList(ADD_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

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

        ItemDocDTO itemDocDTO = setRequestDocDTO();
        itemDocDTO.setDocItems(setDocItemDTOList(ADD_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<ItemDoc> docs = documentService.getDocumentsByType(DocumentType.REQUEST_DOC);
        assertEquals(1, docs.size());

        assertEquals(AUTHOR_ID, docs.get(0).getAuthor().getId());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateCheckDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setCheckDocDTO();
        addTo(itemDocDTO, DOC_ID, DOC_NUMBER);
        itemDocDTO.setCheckInfo(setCHeckInfo(UPDATE_VALUE));
        itemDocDTO.setDocItems(setDocItemDTOList(UPDATE_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(DOC_ID);
        assertEquals(DOC_NUMBER, doc.getNumber());

        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(4, items.size());

        CheckInfo checkInfo = checkInfoService.getCheckInfo(doc);
        assertEquals(CHECK_NUMBER + UPDATE_VALUE, checkInfo.getCheckNumber());
    }

    @Sql(value = "/sql/documents/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateReceiptDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setReceiptDocDTO();
        addTo(itemDocDTO, DOC_ID, DOC_NUMBER);
        itemDocDTO.setDocItems(setDocItemDTOList(UPDATE_VALUE));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/receipt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(DOC_ID);
        assertEquals(DOC_NUMBER, doc.getNumber());

        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        List<Integer> itemIds = items.stream()
                .map(item -> item.getItem().getId())
                .collect(Collectors.toList());

        assertEquals(4, itemIds.size());
        assertTrue(itemIds.containsAll(UPDATE_ITEM_IDS));
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updatePostingDocTest() throws Exception {

        ItemDocDTO itemDocDTO = setPostingDocDTO();
        addTo(itemDocDTO, DOC_ID, DOC_NUMBER);
        itemDocDTO.setDocItems(setDocItemDTOList(UPDATE_VALUE));
        itemDocDTO.setTime(Timestamp.valueOf("2022-01-01 10:30:00"));
        ItemDocRequestDTO requestDTO = setDTO(itemDocDTO);

        this.mockMvc.perform(
                        put(URL_PREFIX + "/posting")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        ItemDoc doc = documentService.getDocumentById(DOC_ID);
        assertEquals(DOC_NUMBER, doc.getNumber());
        assertEquals(1, doc.getDateTime().getDayOfMonth());
        assertEquals(10, doc.getDateTime().getHour());
    }


    private void addTo(ItemDocDTO dto, int docId, int docNumber) {
        dto.setId(docId);
        dto.setNumber(docNumber);
    }

    private ItemDocRequestDTO setDTO(ItemDocDTO itemDocDTO) {
        ItemDocRequestDTO dto = new ItemDocRequestDTO();
        dto.setItemDocDTO(itemDocDTO);
        return dto;
    }

    private ItemDocDTO setItemDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
//        dto.setId();
//        dto.setNumber();
//        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
//        dto.setDocType();
//        dto.setProject(new ProjectDTO());
//        dto.setAuthor();
//        dto.setIndividual();
//        dto.setPaymentType();
//        dto.setAmount();
//        dto.setTax();
//        dto.setPayed();
//        dto.setHold();
//        dto.setDelivery();
//        dto.setSupplier();
//        dto.setRecipient();
//        dto.setStorageFrom();
//        dto.setStorageTo();

        return dto;
    }

    private ItemDocDTO setRequestDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(1));
        dto.setAuthor(setAuthorDTO(AUTHOR_ID));
        dto.setPayed(false);
        dto.setHold(false);
        dto.setStorageTo(setStorageDTO(1));

        return dto;
    }

    private ItemDocDTO setPostingDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(1));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(false);
        dto.setHold(false);
        dto.setRecipient(setCompanyDTO(1));
        dto.setStorageTo(setStorageDTO(1));

        return dto;
    }

    private ItemDocDTO setReceiptDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(RECEIPT_FIELDS_ID));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(true);
        dto.setHold(true);
        dto.setSupplier(setCompanyDTO(2));
        dto.setRecipient(setCompanyDTO(1));
        dto.setStorageTo(setStorageDTO(RECEIPT_FIELDS_ID));

        return dto;
    }

    private ItemDocDTO setCheckDocDTO() {
        ItemDocDTO dto = new ItemDocDTO();
        dto.setTime(new Timestamp(Calendar.getInstance().getTimeInMillis()));
        dto.setProject(setProject(3));
        dto.setAuthor(setAuthorDTO(2));
        dto.setPayed(true);
        dto.setHold(true);
        dto.setIndividual(setIndividualDTO(1));
        dto.setSupplier(setCompanyDTO(1));
        dto.setStorageFrom(setStorageDTO(3));

        return dto;
    }

    private CheckInfoDTO setCHeckInfo(int value) {
        CheckInfoDTO dto = new CheckInfoDTO();
        dto.setCheckNumber(CHECK_NUMBER + value);
        dto.setDateTime(LocalDateTime.now());
        dto.setAmountReceived(1000 * (value + 1));
        dto.setCashRegisterNumber(63214823871L);
        dto.setGuestNumber(1 + value);
        dto.setTableNumber(12 + value);
        dto.setWaiter("Официант 1" + value);
        dto.setReturn(value == 0);
        dto.setKKMChecked(value == 0);
        dto.setPayed(value == 0);
        dto.setPayedByCard(value == 0);
        dto.setDelivery(value == 0);

        return dto;
    }

    private ProjectDTO setProject(int id) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(id);
        return projectDTO;
    }

    private AuthorDTO setAuthorDTO(int id) {
        AuthorDTO authorDTO = new AuthorDTO();
        authorDTO.setId(id);
        return authorDTO;
    }

    private IndividualDTO setIndividualDTO(int id) {
        IndividualDTO individualDTO = new IndividualDTO();
        individualDTO.setId(id);
        return individualDTO;
    }

    private CompanyDTO setCompanyDTO(int id) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(id);
        return companyDTO;
    }

    private StorageDTO setStorageDTO(int id) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(id);
        return storageDTO;
    }

    private List<DocItemDTO> setDocItemDTOList(int value) {
        DocItemDTO first = new DocItemDTO();
        first.setItemId(ADDED_ITEM_IDS.get(0) + value);
        first.setPrice(10.00f * value);
        first.setQuantity(1 + value);
        DocItemDTO second = new DocItemDTO();
        second.setItemId(ADDED_ITEM_IDS.get(1) + value);
        second.setPrice(20.00f * value);
        second.setQuantity(2 + value);
        DocItemDTO third = new DocItemDTO();
        third.setItemId(ADDED_ITEM_IDS.get(2) + value);
        third.setPrice(30.00f * value);
        third.setQuantity(3 + value);
        DocItemDTO forth = new DocItemDTO();
        forth.setItemId(ADDED_ITEM_IDS.get(3) + value);
        forth.setPrice(40.00f * value);
        forth.setQuantity(4 + value);

        return List.of(first, second, third, forth);
    }

}
