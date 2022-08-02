package com.example.store.services;

import com.example.store.components.EnvironmentVars;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.responses.ListResponse;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class DocCrudServiceTest {

    private static final long START = 1640995200000L; // 01/01/2022
    private static final long END = 1656633600000L;  // 01/07/2022

    @Autowired
    private DocCrudService docCrudService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private EnvironmentVars env;

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getPostingDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(1);
        assertEquals("Поступление", dto.getDocType());
        assertEquals(1, dto.getId());
    }

    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getOrderDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(6);
        assertEquals("Расходный кассовый ордер", dto.getDocType());
        assertEquals(6, dto.getId());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteDocumentTest() {
        DocDTO dto = new DocDTO();
        dto.setDocType(DocumentType.POSTING_DOC.getValue());
        dto.setDateTime(1647443208000L); // 16/04/2022
        dto.setId(1);
        env.setPeriodStart();
        docCrudService.softDeleteDocument(dto);
        assertTrue(documentService.getDocumentById(1).isDeleted());
    }

    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteOrderDocumentTest() {
        int docId = 6;
        DocDTO dto = new DocDTO();
        dto.setDocType(DocumentType.CREDIT_ORDER_DOC.getValue());
        dto.setDateTime(1647443208000L); // 16/04/2022
        dto.setId(docId);
        env.setPeriodStart();
        docCrudService.softDeleteDocument(dto);
        assertTrue(documentService.getDocumentById(docId).isDeleted());
    }

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocumentTest() {
        int docId = 1;
        env.setPeriodStart();
        docCrudService.holdDocument(docId);
        assertEquals(2, lotRepository.findAll().size());
        assertEquals(2, lotMoveRepository.findAll().size());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkTimePeriodThrowExceptionTest() {
        DocDTO dto = new DocDTO();
        dto.setDateTime(START);
        env.setPeriodStart();
        assertThrows(BadRequestException.class,
                () -> docCrudService.checkTimePeriod(dto));
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql",
            "/sql/period/addOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkTimePeriodByDocumentThrowExceptionTest() {
        Document document = documentService.getDocumentById(6);
        env.setPeriodStart();
        assertThrows(BadRequestException.class,
                () -> docCrudService.checkTimePeriod(document));
    }
}
