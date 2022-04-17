package com.example.store.services;

import com.example.store.model.dto.ItemQuantityPriceDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
public class Hold1CDocksServiceTest {

    @Autowired
    private Hold1CDocksService hold1CDocksService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;

    @InjectMocks
    private Hold1CDocksService mockedHold1CDocksService;
    @Mock
    private ItemRestService mockedItemRestService;

    private final Comparator<DocumentItem> documentItemComparator
            = Comparator.comparing(item -> item.getItem().getName());

    @Sql(value = "/sql/hold1CDocs/addSixOrders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getUnHoldenOrdersByStorageAndPeriodTest() {
        Project project = projectService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-16T23:59:59.000");
        List<OrderDoc> orders = hold1CDocksService.getUnHoldenOrdersByProjectAndPeriod(project, from, to);
        assertEquals(6, orders.size());
    }

    @Sql(value = "/sql/hold1CDocs/addThreeChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getUnHoldenChecksByStorageAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-16T23:59:59.000");
        List<ItemDoc> checks = hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to);
        assertEquals(3, checks.size());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDocOfTypePostingTest() {
        Project project = projectService.getById(1);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getItemDocOfType(DocumentType.POSTING_DOC, project, time);
        assertEquals(333, itemDoc.getNumber());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDocOfTypeWriteOffTest() {
        Project project = projectService.getById(1);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getItemDocOfType(DocumentType.WRITE_OFF_DOC, project, time);
        assertEquals(222, itemDoc.getNumber());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPostingDocTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc postingDoc = hold1CDocksService.getPostingDoc(storage, project, time);
        assertEquals(DocumentType.POSTING_DOC, postingDoc.getDocType());
        assertEquals(storage, postingDoc.getStorageTo());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getWriteOffDocTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc writeOffDoc = hold1CDocksService.getWriteOffDoc(storage, project, time);
        assertEquals(DocumentType.WRITE_OFF_DOC, writeOffDoc.getDocType());
        assertEquals(storage, writeOffDoc.getStorageFrom());
        assertEquals(project, writeOffDoc.getProject());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getDocumentItemTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getPostingDoc(storage, project, time);
        Item item = itemService.getItemById(7);
        float quantity = 2.22f;
        DocumentItem documentItem = hold1CDocksService.getDocumentItem(itemDoc, item, quantity);
        assertEquals(itemDoc, documentItem.getItemDoc());
        assertEquals(item, documentItem.getItem());
        assertEquals(quantity, documentItem.getQuantity());
    }

    @Sql(value = {"/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createWriteOffDocForChecksTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        Item item7 = itemService.getItemById(7);
        float quantityOf7 = 2.0f;
        Item item8 = itemService.getItemById(8);
        float quantityOf8 = 3.0f;
        Map<Item, Float> itemMap = new HashMap<>();
        itemMap.put(item7, quantityOf7);
        itemMap.put(item8, quantityOf8);
        ItemDoc itemDoc = hold1CDocksService.createWriteOffDocForChecks(storage, project, itemMap, time);
        assertEquals(storage, itemDoc.getStorageFrom());
        assertEquals(project, itemDoc.getProject());
        List<DocumentItem> items = new ArrayList<>(itemDoc.getDocumentItems());
        items.sort(documentItemComparator);
        assertEquals(item7, items.get(0).getItem());
        assertEquals(quantityOf8, items.get(1).getQuantity());
    }

    @Sql(value = {"/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addTwoDocs.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createPostingDocTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        Item item7 = itemService.getItemById(7);
        float quantityOf7 = 2.0f;
        float priceOf7 = 100.00f;
        Item item8 = itemService.getItemById(8);
        float quantityOf8 = 3.0f;
        float priceOf8 = 200.00f;
        List<ItemQuantityPriceDTO> dtoList = List.of(
                getItemQuantityPriceDTO(item7, quantityOf7, priceOf7),
                getItemQuantityPriceDTO(item8, quantityOf8, priceOf8)
        );
        ItemDoc postingDoc = hold1CDocksService.createPostingDoc(storage, project, dtoList, time);
        assertEquals(DocumentType.POSTING_DOC, postingDoc.getDocType());
        List<DocumentItem> docItems = new ArrayList<>(postingDoc.getDocumentItems());
        docItems.sort(documentItemComparator);
        assertEquals(item7, docItems.get(0).getItem());
        assertEquals(quantityOf8, docItems.get(1).getQuantity());
        assertEquals(priceOf7, docItems.get(0).getPrice());
    }

    @Test
    void getNullWhileCreatePostingDocIfDTOListIsNullTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        List<ItemQuantityPriceDTO> dtoList = null;
        ItemDoc postingDoc = hold1CDocksService.createPostingDoc(storage, project, dtoList, time);
        assertNull(postingDoc);
    }

    @Test
    void getNullWhileCreatePostingDocIfDTOListIsEmptyTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        List<ItemQuantityPriceDTO> dtoList = new ArrayList<>();
        ItemDoc postingDoc = hold1CDocksService.createPostingDoc(storage, project, dtoList, time);
        assertNull(postingDoc);
    }

    @Sql(value = "/sql/hold1CDocs/addThreeChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getItemMapFromCheckDocsTest() {
        List<ItemDoc> checks = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        Map<Item, Float> map = hold1CDocksService.getItemMapFromCheckDocs(checks);
        assertFalse(map.isEmpty());
        assertEquals(4, map.size());
        assertEquals(1, map.get(itemService.getItemById(6)));
        assertEquals(3, map.get(itemService.getItemById(9)));
        assertEquals(4, map.get(itemService.getItemById(4)));
        assertEquals(4, map.get(itemService.getItemById(5)));
    }

    @Sql(value = "/sql/hold1CDocs/addThreeChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getPostingItemMapTest() {
        LocalDateTime time = LocalDateTime.now();
        Storage storage = storageService.getById(3);
        List<ItemDoc> checks = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        Map<Item, Float> writeOffItemMap = hold1CDocksService.getItemMapFromCheckDocs(checks);
        Map<Item, Float> itemRestMap = new HashMap<>();
        itemRestMap.put(itemService.getItemById(5), 2.00f);
        itemRestMap.put(itemService.getItemById(6), 10.00f);
        itemRestMap.put(itemService.getItemById(9), 3.00f);
        when(mockedItemRestService.getItemRestMap(writeOffItemMap, storage, time))
                .thenReturn(itemRestMap);
        when(mockedItemRestService.getLastPriceOfItem(any(Item.class))).thenReturn(100.00f);
        List<ItemQuantityPriceDTO> list = mockedHold1CDocksService.getPostingItemMap(writeOffItemMap, storage, time);
        assertEquals(2, list.size());
        assertEquals(4,     list.get(0).getQuantity());
        assertEquals(4,     list.get(0).getItem().getId());
        assertEquals(2,     list.get(1).getQuantity());
        assertEquals(5,     list.get(1).getItem().getId());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createDocsToHoldByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-17T00:00:00.000");
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, from, to);
        assertEquals(DocumentType.POSTING_DOC, hold1CDocksService.getPostingDoc().getDocType());
        assertEquals(DocumentType.WRITE_OFF_DOC, hold1CDocksService.getWriteOffDoc().getDocType());
        List<DocumentItem> postingItems = docItemService.getItemsByDoc(hold1CDocksService.getPostingDoc());
        List<DocumentItem> writeOffItems = docItemService.getItemsByDoc(hold1CDocksService.getWriteOffDoc());
        assertEquals(4, postingItems.size());
        assertEquals(4, writeOffItems.size());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addRestDocs.sql",
            "/sql/hold1CDocs/addRestLots.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createDocsToHoldNoPostingDocByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, from, to);
        assertNull(hold1CDocksService.getPostingDoc());
        assertEquals(DocumentType.WRITE_OFF_DOC, hold1CDocksService.getWriteOffDoc().getDocType());
        List<DocumentItem> postingItems = docItemService.getItemsByDoc(hold1CDocksService.getPostingDoc());
        List<DocumentItem> writeOffItems = docItemService.getItemsByDoc(hold1CDocksService.getWriteOffDoc());
        assertEquals(0, postingItems.size());
        assertEquals(4, writeOffItems.size());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocsAndChecksByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, from, to);
        hold1CDocksService.holdDocsAndChecksByStoragesAndPeriod(storage, from, to);
        List<Document> documents = documentService.getAllDocuments();
        assertEquals(5, documents.size());
        assertEquals(5, documents.stream().filter(Document::isHold).count());
    }

    @Sql(value = {"/sql/hold1CDocs/addSixOrders.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/hold1CDocs/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdOrdersByProjectsAndPeriodTest() {
        Project project = projectService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);
        hold1CDocksService.holdOrdersByProjectsAndPeriod(project, from, to);
        List<Document> documents = documentService.getAllDocuments();
        assertEquals(6, documents.size());
        assertEquals(6, documents.stream().filter(Document::isHold).count());
    }

    private ItemQuantityPriceDTO getItemQuantityPriceDTO(Item item, float quantity, float price) {
        ItemQuantityPriceDTO dto = new ItemQuantityPriceDTO();
        dto.setItem(item);
        dto.setQuantity(quantity);
        dto.setPrice(price);
        return dto;
    }

}
