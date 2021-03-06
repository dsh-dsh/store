package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;

//    todo make not mocked
//    @Test
//    void addDocumentTest() {
//        DocDTO dto = mock(DocDTO.class);
//        dto.setDocType(DocumentType.POSTING_DOC.getValue());
//        mockedDocumentService.addDocument(dto);
//        verify(mockedItemDocFactory, times(1)).addDocument(dto);
//    }
//
//    todo make not mocked
//    @Test
//    void updateDocumentTest() {
//        DocDTO dto = mock(DocDTO.class);
//        mockedDocumentService.updateDocument(dto);
//        verify(mockedItemDocFactory, times(1)).updateDocument(dto);
//    }


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
    void getDocumentsByPeriodTest() {
        Document documentFrom = documentService.getDocumentById(2);
        Document documentTo = documentService.getDocumentById(4);
        List<Document> documents = documentService.getDocumentsByPeriod(documentFrom, documentTo, true);
        assertEquals(3, documents.size());
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
    void getDocumentByIdTest() {
        Document document = documentService.getDocumentById(5);
        assertEquals(5, document.getId());
    }

    @Test
    void getDocumentById_WhenNoSuchDocument_Test() {
        assertThrows(BadRequestException.class,
                () -> documentService.getDocumentById(10));
    }

//    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    @Transactional
//    void getDocDTOByIdTest() {
//        DocDTO dto = documentService.getDocDTOById(1);
//        assertEquals(1, dto.getId());
//    }

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setHoldAndSaveTest() {
        Document document = documentService.getDocumentById(1);
        documentService.setHoldAndSave(false, document);
        assertFalse(document.isHold());
    }

//    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void softDeleteDocumentTest() {
//        DocDTO dto = new DocDTO();
//        dto.setDocType(DocumentType.POSTING_DOC.getValue());
//        dto.setId(1);
//        documentService.softDeleteDocument(dto);
//        assertTrue(documentService.getDocumentById(1).isDeleted());
//    }
//
//    @Sql(value = "/sql/documents/addOrderDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void softDeleteOrderDocumentTest() {
//        int docId = 6;
//        DocDTO dto = new DocDTO();
//        dto.setDocType(DocumentType.CREDIT_ORDER_DOC.getValue());
//        dto.setId(docId);
//        documentService.softDeleteDocument(dto);
//        assertTrue(documentService.getDocumentById(docId).isDeleted());
//    }

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
