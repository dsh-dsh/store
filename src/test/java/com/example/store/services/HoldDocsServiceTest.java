package com.example.store.services;

import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
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
class HoldDocsServiceTest {

    @Autowired
    private HoldDocsService holdDocsService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocumentTest() {
        int docId = 1;
        holdDocsService.holdDocument(docId);
        assertEquals(2, lotRepository.findAll().size());
        assertEquals(2, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/documents/addNotHoldenPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void existsNotHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(3));
        assertTrue(holdDocsService.existsNotHoldenDocsBefore(document));
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void notExistsNotHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(2));
        assertFalse(holdDocsService.existsNotHoldenDocsBefore(document));
    }

}