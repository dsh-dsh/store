package com.example.store.services;

import com.example.store.components.SystemSettingsCash;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.TransactionException;
import com.example.store.model.dto.ItemQuantityPriceDTO;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.CheckPaymentType;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.DocItemRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class Hold1CDocksServiceTest {

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
    @Autowired
    private DocItemRepository docItemRepository;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private LotService lotService;
    @Autowired
    private SystemSettingsCash systemSettingsCash;

    @InjectMocks
    private Hold1CDocksService mockedHold1CDocksService;
    @Mock
    private ItemRestService mockedItemRestService;

    private final Comparator<DocumentItem> documentItemComparator
            = Comparator.comparing(item -> item.getItem().getName());

    @Test
    void holdDocsBeforeIfDocsNotExistsTest() {
        assertDoesNotThrow(() -> hold1CDocksService.holdDocsBefore());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql",
            "/sql/period/addNotHoldenOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocsBeforeIfDocsExistsTest() {
        assertDoesNotThrow(() -> hold1CDocksService.holdDocsBefore());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql",
            "/sql/hold1CDocs/addNotHoldenWriteOffDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void holdDocsBeforeIfDocsExistsThrowTest() {
        assertThrows(TransactionException.class,
                () -> hold1CDocksService.holdDocsBefore());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createSaleOrdersTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-17T00:00:00.000");
        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        hold1CDocksService.createCreditOrders(storage);
        List<OrderDoc> orders = orderDocRepository.findAll();
        assertEquals(3, orders.size());
        Set<PaymentType> set = orders.stream().map(OrderDoc::getPaymentType).collect(Collectors.toSet());
        assertTrue(set.contains(PaymentType.SALE_CASH_PAYMENT));
        assertTrue(set.contains(PaymentType.SALE_CARD_PAYMENT));
        assertTrue(set.contains(PaymentType.SALE_QR_PAYMENT));
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getSumMapTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-17T00:00:00.000");
        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        Map<CheckPaymentType, Float> map = hold1CDocksService.getSumMap();
        assertEquals(3, map.size());
        assertEquals(
                (float) docItemRepository.findAll().stream()
                        .mapToDouble(item -> (item.getPrice()*item.getQuantity().floatValue()) - item.getDiscount()).sum(),
                map.values().stream().reduce(0f, Float::sum));
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createDocsToHoldByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-17T00:00:00.000");
        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        assertEquals(DocumentType.RECEIPT_DOC, hold1CDocksService.getReceiptDoc().getDocType());
        assertEquals(DocumentType.WRITE_OFF_DOC, hold1CDocksService.getWriteOffDoc().getDocType());
        List<DocumentItem> receiptItems = docItemService.getItemsByDoc(hold1CDocksService.getReceiptDoc());
        List<DocumentItem> writeOffItems = docItemService.getItemsByDoc(hold1CDocksService.getWriteOffDoc());
        assertEquals(4, receiptItems.size());
        assertEquals(4, writeOffItems.size());
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createDocsToHoldByStoragesAndPeriodWhenAddRestToHoldIsFalseTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-17T00:00:00.000");

        int currentAddRestForHoldSetting = systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, 0);

        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        assertNull(hold1CDocksService.getReceiptDoc());
        assertEquals(DocumentType.WRITE_OFF_DOC, hold1CDocksService.getWriteOffDoc().getDocType());
        List<DocumentItem> receiptItems = docItemService.getItemsByDoc(hold1CDocksService.getReceiptDoc());
        List<DocumentItem> writeOffItems = docItemService.getItemsByDoc(hold1CDocksService.getWriteOffDoc());
        assertEquals(0, receiptItems.size());
        assertEquals(4, writeOffItems.size());

        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, currentAddRestForHoldSetting);

        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addRestDocs.sql",
            "/sql/hold1CDocs/addRestLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createDocsToHoldNoReceiptDocByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);
        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        assertNull(hold1CDocksService.getReceiptDoc());
        assertEquals(DocumentType.WRITE_OFF_DOC, hold1CDocksService.getWriteOffDoc().getDocType());
        List<DocumentItem> receiptItems = docItemService.getItemsByDoc(hold1CDocksService.getReceiptDoc());
        List<DocumentItem> writeOffItems = docItemService.getItemsByDoc(hold1CDocksService.getWriteOffDoc());
        assertEquals(0, receiptItems.size());
        assertEquals(4, writeOffItems.size());
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void holdDocsAndChecksByStoragesAndPeriodTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);

        int currentAddRestForHoldSetting = systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, 1);

        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        hold1CDocksService.holdDocsAndChecksByStoragesAndPeriod();
        List<Document> documents = documentService.getAllDocuments();
        assertEquals(5, documents.size());
        assertEquals(5, documents.stream().filter(Document::isHold).count());

        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, currentAddRestForHoldSetting);

        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createDocsToHoldInOrderTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);

        int currentAddRestForHoldSetting = systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, 1);

        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        hold1CDocksService.createCreditOrders(storage);
        List<Document> documents = documentService.getAllDocuments();
        documents.sort(Comparator.comparing(Document::getDateTime));
        assertEquals(8, documents.size());
        assertEquals(DocumentType.CHECK_DOC, documents.get(0).getDocType());
        assertEquals(DocumentType.RECEIPT_DOC, documents.get(3).getDocType());
        assertEquals(DocumentType.WRITE_OFF_DOC, documents.get(4).getDocType());
        assertEquals(DocumentType.WITHDRAW_ORDER_DOC, documents.get(5).getDocType());
        assertEquals(DocumentType.WITHDRAW_ORDER_DOC, documents.get(6).getDocType());
        assertEquals(DocumentType.WITHDRAW_ORDER_DOC, documents.get(7).getDocType());

        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, currentAddRestForHoldSetting);

        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addRestDocsWithValue1.sql",
            "/sql/hold1CDocs/addRestLotsWithValue1.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void holdDocsAndChecksByStoragesAndPeriodWhenAddRestToHoldIsTrueAndRestLeakExistsTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);

        int currentAddRestForHoldSetting = systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, 1);

        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        hold1CDocksService.holdDocsAndChecksByStoragesAndPeriod();
        List<Document> documents = documentService.getAllDocuments();
        assertEquals(6, documents.size());
        assertEquals(DocumentType.RECEIPT_DOC, documents.get(3).getDocType());
        assertEquals(hold1CDocksService.getWriteOffDoc(), documents.get(0).getBaseDocument());
        assertEquals(hold1CDocksService.getWriteOffDoc(), documents.get(5).getBaseDocument());
        assertEquals(6, documents.stream().filter(Document::isHold).count());
        List<DocumentItem> docItems = docItemService.getItemsByDoc((ItemDoc) documents.get(5));
        assertEquals(1.4f, Util.floorValue(docItems.get(0).getQuantity().floatValue(), 1));
        assertEquals(7f, docItems.get(1).getQuantity().floatValue());
        assertEquals(6.2f, docItems.get(2).getQuantity().floatValue());
        assertEquals(8f, docItems.get(3).getQuantity().floatValue());

        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, currentAddRestForHoldSetting);

        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = {"/sql/hold1CDocs/addIngredients.sql",
            "/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addRestDocsWithValue5.sql",
            "/sql/hold1CDocs/addRestLotsWithValue5.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void holdDocsAndChecksByStoragesAndPeriodWhenAddRestToHoldIsFalseTest() {
        Storage storage = storageService.getById(3);
        LocalDateTime from = LocalDateTime.now(ZoneId.systemDefault()).withYear(2022).withMonth(3).withDayOfMonth(16).withHour(4);
        LocalDateTime to = from.plusDays(1);

        int currentAddRestForHoldSetting = systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, 0);

        hold1CDocksService.setChecks(hold1CDocksService.getUnHoldenChecksByStorageAndPeriod(storage, from, to));
        hold1CDocksService.setLast1CDocTime();
        hold1CDocksService.createDocsToHoldByStoragesAndPeriod(storage, to);
        hold1CDocksService.holdDocsAndChecksByStoragesAndPeriod();

        List<Document> documents = documentService.getAllDocuments();
        assertEquals(hold1CDocksService.getWriteOffDoc(), documents.get(0).getBaseDocument());
        assertEquals(5, documents.size());
        assertEquals(DocumentType.RECEIPT_DOC, documents.get(3).getDocType());
        assertEquals(5, documents.stream().filter(Document::isHold).count());
        List<DocumentItem> docItems = docItemService.getItemsByDoc((ItemDoc) documents.get(4));
        assertEquals(2.4f, docItems.get(0).getQuantity().floatValue());
        assertEquals(8f, docItems.get(1).getQuantity().floatValue());
        assertEquals(7.2f, docItems.get(2).getQuantity().floatValue());
        assertEquals(9f, docItems.get(3).getQuantity().floatValue());

        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, currentAddRestForHoldSetting);

        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Sql(value = "/sql/hold1CDocs/addSixOrders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @Sql(value = "/sql/hold1CDocs/addThreeChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getReceiptItemMapTest() {
        LocalDateTime time = LocalDateTime.now();
        Storage storage = storageService.getById(3);
        List<ItemDoc> checks = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        Map<Item, BigDecimal> writeOffItemMap = hold1CDocksService.getItemMapFromCheckDocs(checks);
        Map<Item, BigDecimal> itemRestMap = new HashMap<>();
        Item item5 = itemService.getItemById(5);
        Item item6 = itemService.getItemById(6);
        Item item9 = itemService.getItemById(9);
        itemRestMap.put(item5, BigDecimal.valueOf(2.00f));
        itemRestMap.put(item6, BigDecimal.valueOf(10.00f));
        itemRestMap.put(item9, BigDecimal.valueOf(3.00f));
        List<Item> items = new ArrayList<>(writeOffItemMap.keySet());
        when(mockedItemRestService.getItemRestMap(items, storage, time))
                .thenReturn(itemRestMap);
        when(mockedItemRestService.getLastPriceOfItem(any(Item.class), eq(time))).thenReturn(100.00f);
        Map<Item, BigDecimal> map = mockedHold1CDocksService.getReceiptItemMap(writeOffItemMap, storage, time);
        assertEquals(2, map.size());
        Item item4 = itemService.getItemById(4);
        assertEquals(2,     map.get(item5).floatValue());
        assertEquals(4,     map.get(item4).floatValue());
    }

    @Sql(value = "/sql/hold1CDocs/addThreeChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getItemMapFromCheckDocsTest() {
        List<ItemDoc> checks = documentService.getItemDocsByType(DocumentType.CHECK_DOC);
        Map<Item, BigDecimal> map = hold1CDocksService.getItemMapFromCheckDocs(checks);
        assertFalse(map.isEmpty());
        assertEquals(4, map.size());
        assertEquals(1, map.get(itemService.getItemById(6)).floatValue());
        assertEquals(3, map.get(itemService.getItemById(9)).floatValue());
        assertEquals(4, map.get(itemService.getItemById(4)).floatValue());
        assertEquals(4, map.get(itemService.getItemById(5)).floatValue());
    }

    @Sql(value = {"/sql/hold1CDocs/addThreeChecks.sql",
            "/sql/hold1CDocs/addTwoDocs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createReceiptDocTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        Item item7 = itemService.getItemById(7);
        BigDecimal quantityOf7 = BigDecimal.valueOf(2.0f);
        Item item8 = itemService.getItemById(8);
        BigDecimal quantityOf8 = BigDecimal.valueOf(3.0f);
        Map<Item, BigDecimal> map = new HashMap<>();
        map.put(item7, quantityOf7);
        map.put(item8, quantityOf8);
        ItemDoc receiptDoc = hold1CDocksService.createReceiptDoc(storage, project, map, time);
        assertEquals(DocumentType.RECEIPT_DOC, receiptDoc.getDocType());
        List<DocumentItem> docItems = new ArrayList<>(receiptDoc.getDocumentItems());
        docItems.sort(documentItemComparator);
        assertEquals(item7, docItems.get(0).getItem());
        assertEquals(quantityOf8, docItems.get(1).getQuantity());
        assertEquals(0f, docItems.get(0).getPrice());
    }

    @Test
    void getNullWhileCreateReceiptDocIfDTOListIsNullTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc receiptDoc = hold1CDocksService.createReceiptDoc(storage, project, null, time);
        assertNull(receiptDoc);
    }

    @Test
    void getNullWhileCreateReceiptDocIfDTOListIsEmptyTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        Map<Item, BigDecimal> itemMap = new HashMap<>();
        ItemDoc receiptDoc = hold1CDocksService.createReceiptDoc(storage, project, itemMap, time);
        assertNull(receiptDoc);
    }

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createWriteOffDocForChecksTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        Item item7 = itemService.getItemById(7);
        Item item8 = itemService.getItemById(8);
        float quantityOf8 = 3.0f;
        Map<Item, BigDecimal> itemMap = new HashMap<>();
        itemMap.put(item7, BigDecimal.valueOf(2.0f));
        itemMap.put(item8, BigDecimal.valueOf(3.0f));
        hold1CDocksService.setSystemUser(userService.getSystemAuthor());
        ItemDoc itemDoc = hold1CDocksService.createWriteOffDocForChecks(storage, project, itemMap, time);
        assertEquals(storage, itemDoc.getStorageFrom());
        assertEquals(project, itemDoc.getProject());
        List<DocumentItem> items = new ArrayList<>(itemDoc.getDocumentItems());
        items.sort(documentItemComparator);
        assertEquals(item7, items.get(0).getItem());
        assertEquals(quantityOf8, items.get(1).getQuantity().floatValue());
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    @Autowired
    @Qualifier("disabledItemIds")
    private List<Integer> disabledItemIds;


    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void createWriteOffDocForChecksWithDisabledItemTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        Item item7 = itemService.getItemById(7);
        Item item8 = itemService.getItemById(8);
        Map<Item, BigDecimal> itemMap = new HashMap<>();
        itemMap.put(item7, BigDecimal.valueOf(2.0f));
        itemMap.put(item8, BigDecimal.valueOf(3.0f));
        hold1CDocksService.setSystemUser(userService.getSystemAuthor());
        List<Integer> currentList = List.copyOf(disabledItemIds);
        setDisabledItems(List.of(8));
        ItemDoc itemDoc = hold1CDocksService.createWriteOffDocForChecks(storage, project, itemMap, time);
        assertEquals(1, itemDoc.getDocumentItems().size());
        setDisabledItems(currentList);
        hold1CDocksService.setReceiptDoc(null);
        hold1CDocksService.setWriteOffDoc(null);
        hold1CDocksService.setChecks(new ArrayList<>());
    }

    private void setDisabledItems(List<Integer> list) {
        disabledItemIds.clear();
        disabledItemIds.addAll(list);
    }

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getDocumentItemTest() {
        Storage storage = storageService.getById(2);
        Project project = projectService.getById(2);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getReceiptDoc(storage, project, time);
        Item item = itemService.getItemById(7);
        float quantity = 2.22f;
        DocumentItem documentItem = new DocumentItem(itemDoc, item, BigDecimal.valueOf(quantity));
        assertEquals(itemDoc, documentItem.getItemDoc());
        assertEquals(item, documentItem.getItem());
        assertEquals(quantity, documentItem.getQuantity().floatValue());
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

    @Sql(value = "/sql/hold1CDocs/addSixOrders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getUnHoldenOrdersByProjectAndPeriodTest() {
        Project project = projectService.getById(3);
        LocalDateTime from = LocalDateTime.parse("2022-03-16T00:00:00.000");
        LocalDateTime to = LocalDateTime.parse("2022-03-16T23:59:59.000");
        List<OrderDoc> orders = hold1CDocksService.getUnHoldenOrdersByProjectAndPeriod(project, from, to);
        assertEquals(6, orders.size());
    }

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getReceiptDocTest() {
        Storage storage = storageService.getById(3);
        Project project = projectService.getById(3);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc receiptDoc = hold1CDocksService.getReceiptDoc(storage, project, time);
        assertEquals(DocumentType.RECEIPT_DOC, receiptDoc.getDocType());
        assertEquals(storage, receiptDoc.getStorageTo());
    }

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDocOfTypeReceiptTest() {
        Project project = projectService.getById(1);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getItemDocOfType(DocumentType.RECEIPT_DOC, project, time);
        assertEquals(333, itemDoc.getNumber());
    }

    @Sql(value = "/sql/hold1CDocs/addTwoDocs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemDocOfTypeWriteOffTest() {
        Project project = projectService.getById(1);
        LocalDateTime time = LocalDateTime.now();
        ItemDoc itemDoc = hold1CDocksService.getItemDocOfType(DocumentType.WRITE_OFF_DOC, project, time);
        assertEquals(222, itemDoc.getNumber());
    }

    @Sql(value = "/sql/hold1CDocs/addOneCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkUnHoldenDocksExistsIfExistsTest() {
        assertTrue(hold1CDocksService.checkUnHoldenDocksExists(LocalDateTime.now()));
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkUnHoldenDocksExistsTest() {
        assertFalse(hold1CDocksService.checkUnHoldenDocksExists(LocalDateTime.now()));
    }

    @Sql(value = "/sql/documents/add12Checks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getFirstUnHoldenCheckDateTest() {
        assertEquals(Util.getLocalDateTime("15.04.22 00:00:00"), hold1CDocksService.getFirstUnHoldenCheckDate());
    }

    @Sql(value = "/sql/documents/add12Checks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkExistingNotHoldenChecksBeforeTest() {
        LocalDateTime currentDate = Util.getLocalDateTime("15.04.22 00:00:00");
        assertDoesNotThrow(() -> hold1CDocksService.checkExistingNotHoldenChecksBefore(currentDate));
    }

    @Sql(value = "/sql/documents/add12Checks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/hold1CDocs/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkExistingNotHoldenChecksBeforeDoThrowTest() {
        LocalDateTime currentDate = Util.getLocalDateTime("16.04.22 00:00:00");
        assertThrows(BadRequestException.class, () -> hold1CDocksService.checkExistingNotHoldenChecksBefore(currentDate));
    }

    private ItemQuantityPriceDTO getItemQuantityPriceDTO(Item item, float quantity, float price) {
        ItemQuantityPriceDTO dto = new ItemQuantityPriceDTO();
        dto.setItem(item);
        dto.setQuantity(quantity);
        dto.setPrice(price);
        return dto;
    }

}
