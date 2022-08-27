package com.example.store.controllers;

import com.example.store.ItemTestService;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.services.CheckInfoService;
import com.example.store.services.DocItemService;
import com.example.store.services.DocumentService;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
@AutoConfigureMockMvc
class Document1CControllerTest {


    @Autowired
    private TestService testService;
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
    @Autowired
    private CheckInfoService checkInfoService;
    @Autowired
    private DocItemService docItemService;

    private static final String URL_PREFIX = "/api/v1/1с";

    @Sql(value = "/sql/documents/addUnHoldenDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void checkUnHolden1CDocsTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("2022-04-16"));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void checkUnHolden1CDocsNotExistsTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/check"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(""));
    }

    @Test
    void checkUnHolden1CDocsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/check"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addDocsFrom1CTest() throws Exception {
        ItemDocListRequestDTO docListRequestDTO = new ItemDocListRequestDTO();
        docListRequestDTO.setCheckDTOList(getDocDTOList(DocumentType.CHECK_DOC));
        this.mockMvc.perform(
                        post(URL_PREFIX + "/docs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(docListRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> docs = documentService.getAllDocuments();
        assertEquals(5, docs.size());
        ItemDoc check = (ItemDoc) docs.get(0);
        assertEquals(DocumentType.CHECK_DOC, check.getDocType());
        CheckInfo checkInfo = checkInfoService.getCheckInfo(check);
        assertEquals(654321, checkInfo.getCheckNumber());
        List<DocumentItem> documentItemList = docItemService.getItemsByDoc(check);
        assertEquals(4, documentItemList.size());
    }

    @Test
    void addDocsFrom1CUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/docs"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

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
                        post(URL_PREFIX + "/hold"))
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
                        post(URL_PREFIX + "/hold"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void hold1CDocsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    List<DocDTO> getDocDTOList(DocumentType type) {
        List<DocDTO> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DocDTO dto = new DocDTO();
            dto.setNumber(3000000000L + i);
            dto.setDocType(type.getValue());
            dto.setDate("27.08.22 00:00:00");
            dto.setProject(testService.setProject(0, "Жаровня 3"));
            dto.setAuthor(testService.setAuthorDTO(0, "Иванов"));
            dto.setIndividual(testService.setIndividualDTO(0, "Иванов"));
            dto.setSupplier(testService.setCompanyDTO(230902612219L));
            dto.setStorageFrom(testService.setStorageDTO(0, "Жаровня 3"));
            dto.setPayed(false);
            dto.setHold(false);

            String time = "21.08.22 16:1"+ i +":00";
            dto.setCheckInfo(testService.setCHeckInfo(i, time));
            dto.setDocItems(testService.setDocItemDTOList(5));

            list.add(dto);
        }
        return list;
    }
//    {"id": 0,"number":2000000025,"date":"27.08.22 00:00:00",
//            "doc_type":"×åê ÊÊÌ","is_payed":true,"is_hold":false,
//            "is_deleted":false,"is_delivery":false,
//        "author":{"id":0,"name":"Àäìèíèñòðàòîð"},
//        "project":{"id":0,"name":"Æàðîâíÿ 3"},
//        "storage_from":{"id":0,"name":"Æàðîâíÿ 4"},
//        "individual":{"id":0,"name":"Øèïèëîâ Ä.Ì."},
//        "supplier":{"id":0,"name":"ÈÏ Øèïèëîâ Ì.Â.","inn":2309006080},
//        "check_info":{"waiter":"","check_number":0,"cash_register_number":123456789,
//            "amount_received":0,"guest_number":0,"table_number":0,"time":"27.08.22 16:16:07",
//            "is_return":false,"is_KKM_checked":true,"is_payed":true,"is_payed_by_card":false,
//            "is_delivery":false}
//        ,"doc_items": [{"quantity":"1","amount":180,"price":180,"discount":0,
//            "document_id":2000000025,"item_id":3601,"item_name":"Áîðù (1)"}]}
}
