package com.example.store.services;

import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
public class DocumentServiceTest {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private StorageService storageService;

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void existsNotHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(3));
        assertTrue(documentService.existsNotHoldenDocsBefore(document));
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void notExistsNotHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(2));
        assertFalse(documentService.existsNotHoldenDocsBefore(document));
    }

}
