package com.example.store.components;

import com.example.store.model.entities.documents.Document;
import com.example.store.services.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ReHoldDocsTest {

    @Autowired
    private ReHoldDocs reHoldDocs;
    @Autowired
    private DocumentService documentService;

    @Sql(value = "/sql/reHold/addDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/reHold/deleteAll.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void reHolding() {
        Document fromDoc = documentService.getDocumentById(1);
        Document toDoc = documentService.getDocumentById(7);
        reHoldDocs.reHolding(fromDoc, toDoc);
        List<Document> documentList = documentService.getAllDocuments();
        assertEquals(7, documentList.stream().filter(Document::isHold).count());
    }
}