package com.example.store.services;

import com.example.store.controllers.TestService;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocsFrom1cServiceTest {

    @Autowired
    private DocsFrom1cService docsFrom1cService;
    @Autowired
    private TestService testService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocumentWhenCheckDocTest() {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        docDTO.setProject(testService.setProject(3, "Жаровня 3"));
        docDTO.setAuthor(testService.setAuthorDTO(1, "Иванов"));
        docDTO.setSupplier(testService.setCompanyDTO(230902612219L));
        docDTO.setStorageFrom(testService.setStorageDTO(3, "Жаровня 3"));
        docDTO.setCheckInfo( testService.setCHeckInfo(0, "16.03.22 12:00:12"));
        docDTO.setDocItems(testService.setDocItemDTOList(5));
        docDTO.setDate("27.08.22 00:00:00");

        docsFrom1cService.addDocument(docDTO);

        List<Document> docs = documentService.getAllDocuments();
        assertEquals(1, docs.size());
        ItemDoc check = (ItemDoc) docs.get(0);
        assertEquals(DocumentType.CHECK_DOC, check.getDocType());
        CheckInfo checkInfo = checkInfoService.getCheckInfo(check);
        assertEquals(654321, checkInfo.getCheckNumber());
        List<DocumentItem> documentItemList = docItemService.getItemsByDoc(check);
        assertEquals(4, documentItemList.size());

    }

    @Sql(value = "/sql/users/addNotUserWithCode46.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/documents/after.sql",
            "/sql/users/deleteUserCode46.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocumentWhenOrderDocTest() {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        docDTO.setProject(testService.setProject(3, "Жаровня 3"));
        docDTO.setAuthor(testService.setAuthorDTO(1, "Иванов"));
        docDTO.setIndividual(testService.setIndividualDTO(1, 46));
        docDTO.setDate("27.08.22 00:00:00");
        docDTO.setAmount(120f);
        docDTO.setTax(12f);
        docDTO.setSupplier(testService.setCompanyDTO(230902612219L));

        docsFrom1cService.addDocument(docDTO);

        List<Document> docs = documentService.getAllDocuments();
        assertEquals(1, docs.size());
        OrderDoc orderDoc = (OrderDoc) docs.get(0);
        assertEquals(DocumentType.CREDIT_ORDER_DOC, orderDoc.getDocType());
        assertEquals(120f, orderDoc.getAmount());

    }

    @Sql(value = "/sql/users/addNotUserWithCode46.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/documents/after.sql",
            "/sql/users/deleteUserCode46.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocumentWhenInventoryDocTest() {

        DocDTO docDTO = testService.setDTOFields(DocumentType.INVENTORY_DOC);
        docDTO.setProject(testService.setProject(3, "Жаровня 3"));
        docDTO.setAuthor(testService.setAuthorDTO(1, "Иванов"));
        docDTO.setDate("27.08.22 00:00:00");
        docDTO.setStorageFrom(testService.setStorageDTO(3, "Жаровня 3"));
        docDTO.setSupplier(testService.setCompanyDTO(230902612219L));
        docDTO.setDocItems(testService.setDocItemDTOList(5));

        docsFrom1cService.addDocument(docDTO);

        List<Document> docs = documentService.getAllDocuments();
        assertEquals(1, docs.size());
        ItemDoc itemDoc = (ItemDoc) docs.get(0);
        assertEquals(DocumentType.INVENTORY_DOC, itemDoc.getDocType());
        List<DocumentItem> documentItemList = docItemService.getItemsByDoc(itemDoc);
        assertEquals(4, documentItemList.size());

    }

    @Sql(value = {"/sql/users/addNotUserWithCode46.sql",
            "/sql/documents/addOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/documents/after.sql",
            "/sql/users/deleteUserCode46.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void skipAddDocumentWhenDocWithNumberExistsTest() {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        docDTO.setNumber(6L);
        docDTO.setProject(testService.setProject(3, "Жаровня 3"));
        docDTO.setAuthor(testService.setAuthorDTO(1, "Иванов"));
        docDTO.setIndividual(testService.setIndividualDTO(1, 46));
        docDTO.setDate("27.08.22 00:00:00");
        docDTO.setAmount(120f);
        docDTO.setTax(12f);
        docDTO.setSupplier(testService.setCompanyDTO(230902612219L));

        docsFrom1cService.addDocument(docDTO);
        List<Document> docs = documentService.getAllDocuments();
        assertEquals(1, docs.size());
        OrderDoc orderDoc = (OrderDoc) docs.get(0);
        assertEquals(DocumentType.CREDIT_ORDER_DOC, orderDoc.getDocType());
        assertEquals(6L, orderDoc.getNumber());

    }

    @Sql(value = "/sql/documents/addCheckDocOnly.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocItemsTest() {

        DocDTO docDTO = testService.setDTOFields(DocumentType.CHECK_DOC);
        docDTO.setIndividual(testService.setIndividualDTO(1));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setStorageFrom(testService.setStorageDTO(3));
        docDTO.setCheckInfo(testService.setCHeckInfo(0));
        docDTO.setDocItems(testService.setDocItemDTOList(5));

        ItemDoc check = (ItemDoc) documentService.getDocumentById(1);

        docsFrom1cService.addDocItems(docDTO, check);
        List<DocumentItem> documentItemList = docItemService.getItemsByDoc(check);
        assertEquals(4, documentItemList.size());

    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void isDocNumberExistsTest() {
        assertTrue(docsFrom1cService.isDocNumberExists(4L, DocumentType.RECEIPT_DOC));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void isDocNumberNotExistsTest() {
        assertFalse(docsFrom1cService.isDocNumberExists(5L, DocumentType.RECEIPT_DOC));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewTimeDocDateTimeExistsTest() {
        LocalDate date = LocalDate.parse("2022-03-16");
        docsFrom1cService.setDocDateTime(LocalDateTime.parse("2022-03-16T11:30:36.596"));
        assertEquals(LocalDateTime.parse("2022-03-16T11:30:36.597"), docsFrom1cService.getNewTime(date));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewTimeTest() {
        docsFrom1cService.setDocDateTime(null);
        LocalDate date = LocalDate.parse("2022-03-16");
        assertEquals(LocalDateTime.parse("2022-03-16T11:30:36.396"), docsFrom1cService.getNewTime(date));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNewTimeWhenNextDateTest() {
        LocalDate date = LocalDate.parse("2022-03-17");
        docsFrom1cService.setDocDateTime(LocalDateTime.parse("2022-03-16T11:30:36.596"));
        assertEquals(LocalDateTime.parse("2022-03-17T01:00:00.001"), docsFrom1cService.getNewTime(date));
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void getNewTimeWhenNoDocsTest() {
        LocalDate date = LocalDate.parse("2022-03-16");
        docsFrom1cService.setDocDateTime(null);
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.001"), docsFrom1cService.getNewTime(date));
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getLastDocTimeTest() {
        LocalDate date = LocalDate.parse("2022-03-16");
        assertEquals(LocalDateTime.parse("2022-03-16T11:30:36.395"), docsFrom1cService.getLastDocTime(date));
    }

    @Test
    void getLastDocTimeWhenNoDocsTest() {
        LocalDate date = LocalDate.parse("2022-03-16");
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.000"), docsFrom1cService.getLastDocTime(date));
    }
}