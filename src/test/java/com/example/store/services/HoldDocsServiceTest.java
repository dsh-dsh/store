package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.WarningException;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import org.hibernate.persister.walking.spi.WalkingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class HoldDocsServiceTest {

    @Autowired
    private HoldDocsService holdDocsService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void possibleToHoldThenNotExistsHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(3));
        assertTrue(holdDocsService.checkPossibilityToHold(document));
    }

    @Sql(value = "/sql/documents/addDocsForSerialHold.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void notPossibleToHoldThenExistsNotHoldenDocsBeforeTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(3));
        WarningException exception = assertThrows(WarningException.class,
                () -> holdDocsService.checkPossibilityToHold(document));
        assertEquals(Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE, exception.getMessage());
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void notPossibleToUnHoldThenExistsHoldenDocsAfterTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.parse("2022-01-01T10:00:00.000"));
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(2));
        document.setHold(true);
        WarningException exception = assertThrows(WarningException.class,
                () -> holdDocsService.checkPossibilityToHold(document));
        assertEquals(Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE, exception.getMessage());
    }

    // tod
    @Test
    void possibleToUnHoldThenNotExistsHoldenDocsAfterTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(2));
        document.setHold(true);
        assertTrue(holdDocsService.checkPossibilityToHold(document));
    }

}