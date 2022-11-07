package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.WarningException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.annotations.Transaction;
import org.hibernate.persister.walking.spi.WalkingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @Autowired
    private ItemService itemService;

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

    @Test
    void possibleToUnHoldThenNotExistsHoldenDocsAfterTest() {
        ItemDoc document = new ItemDoc();
        document.setDateTime(LocalDateTime.now());
        document.setDocType(DocumentType.POSTING_DOC);
        document.setStorageTo(storageService.getById(2));
        document.setHold(true);
        assertTrue(holdDocsService.checkPossibilityToHold(document));
    }

    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkDocItemQuantitiesTest() {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setDateTime(LocalDateTime.now());
        itemDoc.setDocType(DocumentType.WRITE_OFF_DOC);
        itemDoc.setStorageFrom(storageService.getById(1));
        Item item1 = itemService.getItemById(7);
        Item item2 = itemService.getItemById(8);
        Set<DocumentItem> items = Set.of(
                new DocumentItem(itemDoc, item1, BigDecimal.ONE),
                new DocumentItem(itemDoc, item2, BigDecimal.TEN)
        );
        itemDoc.setDocumentItems(items);
        assertThrows(WarningException.class, () -> holdDocsService.checkDocItemQuantities(itemDoc));
    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql", "/sql/documents/holdDocsForSerialUnHold.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkDocItemQuantitiesWhenExistsTest() {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setDateTime(LocalDateTime.now());
        itemDoc.setDocType(DocumentType.WRITE_OFF_DOC);
        itemDoc.setStorageFrom(storageService.getById(3));
        Item item1 = itemService.getItemById(7);
        Item item2 = itemService.getItemById(8);
        Set<DocumentItem> items = Set.of(
                new DocumentItem(itemDoc, item1, BigDecimal.ONE),
                new DocumentItem(itemDoc, item2, BigDecimal.ONE)
        );
        itemDoc.setDocumentItems(items);
        assertDoesNotThrow(() -> holdDocsService.checkDocItemQuantities(itemDoc));
    }
}