package com.example.store.components;

import com.example.store.model.entities.documents.Document;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.services.DocumentService;
import com.example.store.services.Hold1CDocksService;
import com.example.store.services.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class UnHoldDocsTest {

    @Autowired
    private UnHoldDocs unHoldDocs;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private Hold1CDocksService hold1CDocksService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldAllDocsAfter_DocWithId_1_Test() {
        Document document = documentService.getDocumentById(1);
        unHoldDocs.unHoldAllDocsAfter(document);
        assertEquals(0, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(0, lotRepository.findAll().size());
        assertEquals(0, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldAllDocsAfter_DocWithId_2_Test() {
        Document document = documentService.getDocumentById(2);
        unHoldDocs.unHoldAllDocsAfter(document);
        assertEquals(1, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(2, lotRepository.findAll().size());
        assertEquals(2, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldAllDocsAfter_DocWithId_4_Test() {
        Document document = documentService.getDocumentById(4);
        unHoldDocs.unHoldAllDocsAfter(document);
        assertEquals(3, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(4, lotRepository.findAll().size());
        assertEquals(6, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldAllDocsAfter_DocWithId_5_Test() {
        Document document = documentService.getDocumentById(5);
        unHoldDocs.unHoldAllDocsAfter(document);
        assertEquals(4, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(4, lotRepository.findAll().size());
        assertEquals(10, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldDocument_WithId_3_Test() {
        Document document = documentService.getDocumentById(3);
        unHoldDocs.unHoldDocument(document);
        assertEquals(4, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(4, lotRepository.findAll().size());
        assertEquals(12, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void unHoldDocument_WithId_4_Test() {
        Document document = documentService.getDocumentById(4);
        unHoldDocs.unHoldDocument(document);
        assertEquals(4, documentService.getAllDocuments().stream().filter(Document::isHold).count());
        assertEquals(4, lotRepository.findAll().size());
        assertEquals(10, lotMoveRepository.findAll().size());
    }
}