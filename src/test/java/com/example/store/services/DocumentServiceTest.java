package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Period;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private PeriodService periodService;

    @Sql(value = "/sql/documents/addSomeCheckDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getFirstUnHoldenCheckTest() {
        Period period = periodService.getCurrentPeriod();
        Document document = documentService.getFirstUnHoldenCheck(period.getStartDate().atStartOfDay());
        assertEquals(4, document.getId());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentsAfterAndIncludeTest() {
        Document document = documentService.getDocumentById(1);
        List<Document> documents = documentService.getDocumentsAfterAndInclude(document);
        assertEquals(5, documents.size());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void existsHoldenDocumentsAfterTest() {
        assertTrue(documentService.existsHoldenDocumentsAfter(Util.getLocalDateTime("01.01.22 00:00:00")));
    }
    @Test
    void existsHoldenDocumentsAfterFalseTest() {
        assertFalse(documentService.existsHoldenDocumentsAfter(Util.getLocalDateTime("01.01.22 00:00:00")));
    }

    @Sql(value = "/sql/lots/addDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentsByTypeAndStorageAndIsHoldTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-01T00:00:00.000");
        LocalDateTime to = LocalDateTime.now();
        List<ItemDoc> documents = documentService.getDocumentsByTypeAndStorageAndIsHold(DocumentType.WRITE_OFF_DOC, storage, true, from, to);
        assertEquals(2, documents.size());
        assertEquals(3, documents.get(0).getId());
        assertEquals(4, documents.get(1).getId());
    }

    @Sql(value = {"/sql/orders/addCreditDoc.sql", "/sql/orders/addWithdrawDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentsByTypeInAndProjectAndIsHold_WhenCreditOrder_Test() {
        Project project = projectService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-01T00:00:00.000");
        LocalDateTime to = LocalDateTime.now();
        List<DocumentType> types = List.of(DocumentType.CREDIT_ORDER_DOC);
        List<OrderDoc> documents = documentService.getDocumentsByTypeInAndProjectAndIsHold(types, project, true, from, to);
        assertEquals(1, documents.size());
    }

    @Sql(value = {"/sql/orders/addCreditDoc.sql", "/sql/orders/addWithdrawDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentsByTypeInAndProjectAndIsHold_WhenCreditOrderAndWithdrawOrder_Test() {
        Project project = projectService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-01T00:00:00.000");
        LocalDateTime to = LocalDateTime.now();
        List<DocumentType> types = List.of(DocumentType.CREDIT_ORDER_DOC, DocumentType.WITHDRAW_ORDER_DOC);
        List<OrderDoc> documents = documentService.getDocumentsByTypeInAndProjectAndIsHold(types, project, true, from, to);
        assertEquals(2, documents.size());
    }

    @Sql(value = "/sql/lots/addDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDocsByTypeTest() {
        List<ItemDoc> documents = documentService.getItemDocsByType(DocumentType.RECEIPT_DOC);
        assertEquals(2, documents.size());
        assertEquals(1, documents.get(0).getId());
        assertEquals(2, documents.get(1).getId());
    }

    @Sql(value = "/sql/lots/addDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentsByTypesAndProjectTest() {
        List<DocumentType> types = List.of(DocumentType.RECEIPT_DOC, DocumentType.WRITE_OFF_DOC);
        Project project = projectService.getById(3);
        LocalDateTime start = Util.getLocalDateTime("01.03.22 00:00:00");
        LocalDateTime end = Util.getLocalDateTime("06.03.22 00:00:00");
        List<Document> documents = documentService.getDocumentsByTypesAndProject(types, project, start, end);
        assertEquals(2, documents.size());
    }

    @Sql(value = "/sql/lots/addDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocumentByIdTest() {
        Document document = documentService.getDocumentById(5);
        assertEquals(5, document.getId());
    }

    @Test
    void getDocumentById_WhenNoSuchDocument_Test() {
        assertThrows(BadRequestException.class,
                () -> documentService.getDocumentById(10));
    }

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setHoldAndSaveTest() {
        Document document = documentService.getDocumentById(1);
        documentService.setIsHoldAndSave(false, document);
        assertFalse(document.isHold());
    }

    @Sql(value = {"/sql/documents/addOrderDoc.sql",
            "/sql/documents/addPostingDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNextDocumentNumberWhenDocOfSuchTypeExistsTest() {
        int newOrderNumber = documentService.getNextDocumentNumber(DocumentType.CREDIT_ORDER_DOC);
        int newPostingNumber = documentService.getNextDocumentNumber(DocumentType.POSTING_DOC);
        assertEquals(7, newOrderNumber);
        assertEquals(2, newPostingNumber);
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNextDocumentNumberWhenDocNotExistsTest() {
        int newNumber = documentService.getNextDocumentNumber(DocumentType.CREDIT_ORDER_DOC);
        assertEquals(1, newNumber);
    }
}
