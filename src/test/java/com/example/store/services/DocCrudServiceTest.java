package com.example.store.services;

import com.example.store.components.PeriodStartDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
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
    private PeriodStartDateTime periodStartDateTime;

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getPostingDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(1, false);
        assertEquals("Поступление", dto.getDocType());
        assertEquals(1, dto.getId());
    }

    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getOrderDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(6, false);
        assertEquals("Расходный кассовый ордер", dto.getDocType());
        assertEquals(6, dto.getId());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getCopyPostingDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(1, true);
        assertEquals("Поступление", dto.getDocType());
        assertEquals(0, dto.getId());
        assertEquals(2, dto.getNumber());
        assertFalse(dto.isHold());
        assertEquals(LocalDate.now(), Util.getLocalDate(dto.getDateTime()));
    }

    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getCopyOrderDocDTOByIdTest() {
        DocDTO dto = docCrudService.getDocDTOById(6, true);
        assertEquals("Расходный кассовый ордер", dto.getDocType());
        assertEquals(0, dto.getId());
        assertEquals(7, dto.getNumber());
        assertFalse(dto.isHold());
        assertEquals(LocalDate.now(), Util.getLocalDate(dto.getDateTime()));
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteDocumentTest() {
        DocDTO dto = new DocDTO();
        dto.setDocType(DocumentType.POSTING_DOC.getValue());
        dto.setDateTime(1647443208000L); // 16/04/2022
        dto.setId(1);
        periodStartDateTime.setPeriodStart();
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
        periodStartDateTime.setPeriodStart();
        docCrudService.softDeleteDocument(dto);
        assertTrue(documentService.getDocumentById(docId).isDeleted());
    }

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocumentTest() {
        int docId = 1;
        periodStartDateTime.setPeriodStart();
        docCrudService.holdDocument(docId);
        assertEquals(2, lotRepository.findAll().size());
        assertEquals(2, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/documents/addChecksAndBaseDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteBaseDocsTest() {
        List<Document> docs = documentService.getAllDocuments();
        List<LocalDate> dates = List.of(LocalDate.parse("2022-04-16"));
        docCrudService.softDeleteBaseDocs(docs, dates);
        docs = documentService.getAllDocuments();
        assertFalse(docs.get(0).isDeleted());
        assertNull(docs.get(0).getBaseDocument());
        assertFalse(docs.get(1).isDeleted());
        assertNull(docs.get(1).getBaseDocument());
        assertFalse(docs.get(2).isDeleted());
        assertNull(docs.get(2).getBaseDocument());
        assertTrue(docs.get(3).isDeleted());
        assertTrue(docs.get(4).isDeleted());
        assertTrue(docs.get(5).isDeleted());
    }

    @Sql(value = "/sql/documents/addTenChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDatesOfChecksTest() {
        List<Document> docs = documentService.getAllDocuments();
        List<LocalDate> dates = docCrudService.getDatesOfChecks(docs);
        assertEquals(2, dates.size());
        assertEquals(LocalDate.parse("2022-04-15"), dates.get(0));
        assertEquals(LocalDate.parse("2022-04-16"), dates.get(1));
    }

    @Sql(value = "/sql/documents/addChecksAndBaseDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAllChecksToUnHoldTest() {
        Document check = documentService.getDocumentById(2);
        List<Document> docs = documentService.getDocumentsAfterAndInclude(check);
        docs = docCrudService.getAllChecksToUnHold(docs);
        assertEquals(5, docs.size());
    }

    @Sql(value = "/sql/documents/addUnHoldenDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkingFogUnHoldenChecksTest() {
        List<Document> docs = documentService.getAllDocuments();
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> docCrudService.checkingFogUnHoldenChecks(docs));
        assertEquals(Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE, exception.getMessage());
    }


    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkTimePeriodThrowExceptionTest() {
        DocDTO dto = new DocDTO();
        dto.setDateTime(START);
        periodStartDateTime.setPeriodStart();
        assertThrows(BadRequestException.class,
                () -> docCrudService.checkTimePeriod(dto));
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkTimePeriodByDocumentThrowExceptionTest() {
        Document document = documentService.getDocumentById(6);
        periodStartDateTime.setPeriodStart();
        assertThrows(BadRequestException.class,
                () -> docCrudService.checkTimePeriod(document));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewDocNumberWhenCheckDocTest() {
        assertEquals(2, docCrudService.getNewDocNumber(DocumentType.CHECK_DOC.getValue()));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewDocNumberWhenRequestDocTest() {
        assertEquals(12, docCrudService.getNewDocNumber(DocumentType.REQUEST_DOC.getValue()));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNextDocTimeWhenDocsExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        Sort sort = Sort.by(Constants.DATE_TIME_STRING).descending();
        boolean next = true;
        assertEquals(LocalDateTime.parse("2022-03-16T11:30:36.396"), docCrudService.getDocTime(docDate, sort, next));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPreviousDocTimeWhenDocsExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        Sort sort = Sort.by(Constants.DATE_TIME_STRING);
        boolean next = false;
        assertEquals(LocalDateTime.parse("2022-03-16T06:30:36.394"), docCrudService.getDocTime(docDate, sort, next));
    }

    @Test
    void getNextDocTimeWhenDocsNotExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        Sort sort = Sort.by(Constants.DATE_TIME_STRING).descending();
        boolean next = true;
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.000"), docCrudService.getDocTime(docDate, sort, next));
    }

    @Test
    void getPreviousDocTimeWhenDocsNotExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        Sort sort = Sort.by(Constants.DATE_TIME_STRING);
        boolean next = false;
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.000"), docCrudService.getDocTime(docDate, sort, next));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkUnHoldenChecksTest() {
        assertEquals("", docCrudService.checkUnHoldenChecks());
    }

    @Sql(value = "/sql/documents/addUnHoldenDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkUnHoldenChecksExistsTest() {
        assertEquals("2022-04-16", docCrudService.checkUnHoldenChecks());
    }

}
