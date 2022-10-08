package com.example.store.controllers;

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
import com.example.store.utils.Constants;
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

import java.time.LocalDateTime;
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
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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

    private static final String URL_PREFIX = "/api/v1/1c";

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

    @Sql(value = "/sql/documents/addUnHoldenDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getLast1CDocNumberTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/last/doc")
                                .param("prefix", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("<3000000003>CHECK_DOC*"));
    }

    @Test
    void getLast1CDocNumberUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/last/doc")
                                .param("prefix", "3"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addDocsFrom1CTest() throws Exception {
        ItemDocListRequestDTO docListRequestDTO = new ItemDocListRequestDTO();
        docListRequestDTO.setDocDTOList(getDocDTOList());
        this.mockMvc.perform(
                        post(URL_PREFIX + "/docs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(docListRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> docs = documentService.getAllDocuments();
        assertEquals(7, docs.size());
        ItemDoc check = (ItemDoc) docs.get(0);
        assertEquals(DocumentType.CHECK_DOC, check.getDocType());
        CheckInfo checkInfo = checkInfoService.getCheckInfo(check);
        assertEquals(654321, checkInfo.getCheckNumber());
        List<DocumentItem> documentItemList = docItemService.getItemsByDoc(check);
        assertEquals(4, documentItemList.size());
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addInventoryDocFrom1CTest() throws Exception {
        ItemDocListRequestDTO docListRequestDTO = new ItemDocListRequestDTO();
        docListRequestDTO.setDocDTOList(getInventoryDocDTOList());
        this.mockMvc.perform(
                        post(URL_PREFIX + "/docs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(docListRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<Document> docs = documentService.getAllDocuments();
        assertEquals(1, docs.size());
        ItemDoc check = (ItemDoc) docs.get(0);
        assertEquals(DocumentType.INVENTORY_DOC, check.getDocType());
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(Constants.NOT_HOLDEN_CHECKS_DOCS_NOT_EXIST_MESSAGE));
    }

    @Test
    void hold1CDocsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/hold"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    List<DocDTO> getDocDTOList() {
        List<DocDTO> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DocDTO dto = new DocDTO();
            dto.setNumber(3000000000L + i);
            dto.setDocType(DocumentType.CHECK_DOC.getValue());
            dto.setDate("27.08.22 00:00:00");
            dto.setProject(testService.setProject(0, "Жаровня 3"));
            dto.setAuthor(testService.setAuthorDTO(0, "Иванов"));
            dto.setSupplier(testService.setCompanyDTO("230902612219"));
            dto.setStorageFrom(testService.setStorageDTO(0, "Жаровня 3"));
            dto.setPayed(false);
            dto.setHold(false);

            String time = "21.08.22 16:1" + i + ":00";
            dto.setCheckInfo(testService.setCHeckInfo(i, time));
            dto.setDocItems(testService.setDocItemDTOList(5));

            list.add(dto);
        }
        for (int i = 5; i < 7; i++) {
            DocDTO dto = new DocDTO();
            dto.setNumber(3000000000L + i);
            dto.setDocType(DocumentType.CREDIT_ORDER_DOC.getValue());
            dto.setDate("27.08.22 00:00:00");
            dto.setProject(testService.setProject(0, "Жаровня 3"));
            dto.setAuthor(testService.setAuthorDTO(0, "Иванов"));
            dto.setIndividual(testService.setIndividualDTO(0, 4));
            dto.setSupplier(testService.setCompanyDTO("230902612219"));
            dto.setStorageFrom(testService.setStorageDTO(0, "Жаровня 3"));
            dto.setPayed(false);
            dto.setHold(false);
            dto.setAmount(1000f);
            dto.setTax(1000f);

            list.add(dto);
        }
        return list;
    }

    List<DocDTO> getInventoryDocDTOList() {
        List<DocDTO> list = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            DocDTO dto = new DocDTO();
            dto.setNumber(3000000000L + i);
            dto.setDocType(DocumentType.INVENTORY_DOC.getValue());
            dto.setDate("27.08.22 00:00:00");
            dto.setProject(testService.setProject(0, "Жаровня 3"));
            dto.setAuthor(testService.setAuthorDTO(0, "Иванов"));
            dto.setSupplier(testService.setCompanyDTO("230902612219"));
            dto.setStorageFrom(testService.setStorageDTO(0, "Жаровня 3"));
            dto.setPayed(false);
            dto.setHold(false);
            dto.setDocItems(testService.setDocItemDTOList(5));

            list.add(dto);
        }
        return list;
    }

}
