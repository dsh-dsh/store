package com.example.store.services;

import com.example.store.components.PeriodDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class LotServiceTest {

    @Autowired
    private LotService lotService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private PeriodDateTime periodDateTime;
    @Autowired
    private ItemService itemService;
    @Autowired
    private StorageService storageService;

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotByDocumentItem_WhenPostingDoc_Test() {
        DocumentItem item = docItemService.getItemById(1);
        Lot lot = lotService.getLotByDocumentItemForPosting(item);
        assertEquals(1, lot.getId());
    }

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotByDocumentItem_WhenWriteOffDoc_Test() {
        DocumentItem item = docItemService.getItemById(3);
        Lot lot = lotService.getLotsByDocumentItemForStorageDocs(item).get(0);
        assertEquals(1, lot.getId());
    }

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotByDocumentItem_WhenMovementDoc_Test() {
        DocumentItem item = docItemService.getItemById(5);
        List<Lot> lots = lotService.getLotsByDocumentItemForStorageDocs(item);
        assertEquals(2, lots.size());
        assertEquals(1, lots.get(0).getId());
        assertEquals(2, lots.get(1).getId());
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotMapThanTime20220320Test() {
        periodDateTime.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, BigDecimal> map = lotService.getLotMap(getDocItem(8, 2.00f), getStorage(3), endTime);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(2.00f))));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotMapThanTime20220311Test() {
        periodDateTime.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, BigDecimal> map = lotService.getLotMap(getDocItem(8, 10.00f), getStorage(3), endTime);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(4.000).setScale(3, RoundingMode.CEILING))));
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(6.000).setScale(3, RoundingMode.CEILING))));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getOneLotOfItemThanTime20220320Time() {
        periodDateTime.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, BigDecimal> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), endTime);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(4.00f).setScale(3, RoundingMode.CEILING))));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getTwoLotsOfItemThanTime20220310Test() {
        periodDateTime.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, BigDecimal> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), endTime);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(4.00f).setScale(3, RoundingMode.CEILING))));
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(10.00f).setScale(3, RoundingMode.CEILING))));
    }

    @Test
    void getLotMapOfOneToHoldTest() {
        Map<Lot, BigDecimal> map = lotService.getLotMapToHold(getMapOfLotAndBigDecimal(), BigDecimal.valueOf(5.00f));
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(5.00f).setScale(1, RoundingMode.CEILING))));
    }

    @Test
    void getLotMapOfTwoToHoldTest() {
        Map<Lot, BigDecimal> map = lotService.getLotMapToHold(getMapOfLotAndBigDecimal(), BigDecimal.valueOf(12.00f));
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(5.00f).setScale(1, RoundingMode.CEILING))));
        assertThat(map, hasValue(equalTo(BigDecimal.valueOf(7.00f).setScale(1, RoundingMode.CEILING))));
    }

    @Test
    void checkQuantityShortageExceptionThrownTest() {
        Map<Lot, BigDecimal> mapOfLotAndBigDecimal = getMapOfLotAndBigDecimal();
        Item item = getItem(8);
        assertThrows(BadRequestException.class,
                () -> itemRestService.checkQuantityShortage(item, mapOfLotAndBigDecimal, BigDecimal.valueOf(20.00f)));
    }

    @Sql(value = "/sql/lots/addTwoDocsAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfWriteOffDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(2);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(9, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageFrom(), LocalDateTime.now()).floatValue());
        assertEquals(8, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageFrom(), LocalDateTime.now()).floatValue());
    }

    @Sql(value = "/sql/lots/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfReceiptDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(5, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()).floatValue());
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()).floatValue());
    }

    @Sql(value = "/sql/lots/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfPostingDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(13, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()).floatValue());
        assertEquals(8, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()).floatValue());
    }

    @Sql(value = "/sql/lots/addPostingDocWithLotsAndMovementDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfMovementDocTest() {
        ItemDoc movementDocument = (ItemDoc) documentService.getDocumentById(2);
        lotService.addLotMovements(movementDocument);
        List<Item> items = movementDocument.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(4, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageFrom(), LocalDateTime.now()).floatValue());
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageFrom(), LocalDateTime.now()).floatValue());
        assertEquals(6, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageTo(), LocalDateTime.now()).floatValue());
        assertEquals(7, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageTo(), LocalDateTime.now()).floatValue());
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addTwoDocsAndLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addStorageDocMovement_whenWriteOffDoc_Test() {
        periodDateTime.setPeriodStart();
        DocumentItem item = docItemService.getItemById(3);
        lotService.addStorageDocMovement(item);
        assertEquals(1, lotService.getLotsByDocumentItemForStorageDocs(item).get(0).getId());
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addPostingDocWithLotsAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addStorageDocMovement_whenMovementDoc_Test() {
        periodDateTime.setPeriodStart();
        DocumentItem item = docItemService.getItemById(3);
        lotService.addStorageDocMovement(item);
        List<Lot> lots = lotService.getLotsByDocumentItemForStorageDocs(item);
        assertEquals(1, lotService.getLotsByDocumentItemForStorageDocs(item).get(0).getId());
    }

    @Test
    void checkQuantityShortageNoExceptionTest() {
        Map<Lot, BigDecimal> mapOfLotAndBigDecimal = getMapOfLotAndBigDecimal();
        Item item = getItem(8);
        assertDoesNotThrow(
                () -> itemRestService.checkQuantityShortage(item, mapOfLotAndBigDecimal, BigDecimal.valueOf(15.00f)));
    }

    @Sql(value = "/sql/lots/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotsTest() {
        Document document = documentService.getDocumentById(1);
        lotService.addLots(document);
        assertEquals(2, lotRepository.findAll().size());
        assertEquals(2, lotMoveRepository.findAll().size());
    }

    @Sql(value = "/sql/lots/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotTest() {
        DocumentItem documentItem = docItemService.getItemById(1);
        lotService.addLot(documentItem);
        assertEquals(1, lotRepository.findAll().size());
        assertEquals(1, lotMoveRepository.findAll().size());
    }

    @Sql(value = {"/sql/lots/addPostingDoc.sql", "/sql/lots/addLotsOnly.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void removeLotsTest() {
        Document document = documentService.getDocumentById(1);
        List<DocumentItem> documentItems = docItemService.getItemsByDoc((ItemDoc)document);
        lotService.removeLotsForItems(documentItems);
        assertEquals(0, lotRepository.findAll().size());
    }

    @Sql(value = {"/sql/lots/addPostingDoc.sql", "/sql/lots/addLotsOnly.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void removeLotTest() {
        DocumentItem documentItem = docItemService.getItemById(1);
        lotService.removeLot(documentItem);
        assertEquals(1, lotRepository.findAll().size());
    }

    @Test
    void setAveragePriceTest() {
        DocumentItem docItem = new DocumentItem();
        docItem.setPrice(200.00f);
        docItem.setQuantity(BigDecimal.valueOf(15.00f));
        Map<Lot, BigDecimal> lotMap = getMapOfLotAndBigDecimal();
        lotService.setAveragePrice(docItem, lotMap);
        float expectedPrice = ((float)(50*5 + 60*10)) / 15;
        assertEquals(Util.floorValue(expectedPrice, 2), docItem.getPrice());
    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql", "/sql/documents/holdDocsForSerialUnHold.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getShortageMapOfItemsNoShortageTest() {
        ItemDoc itemDoc = getItemDoc(1);
        Map<Item, BigDecimal> map = lotService.getShortageMapOfItems(
                itemDoc.getDocumentItems(), itemDoc.getStorageFrom(), itemDoc.getDateTime());
        assertTrue(map.isEmpty());
    }

    @Sql(value = {"/sql/documents/addDocsForSerialHold.sql", "/sql/documents/holdDocsForSerialUnHold.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getShortageMapOfItemsTest() {
        ItemDoc itemDoc = getItemDoc(10);
        Map<Item, BigDecimal> map = lotService.getShortageMapOfItems(
                itemDoc.getDocumentItems(), itemDoc.getStorageFrom(), itemDoc.getDateTime());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(-8).setScale(3, RoundingMode.HALF_EVEN), map.get(itemService.getItemById(7)));
        assertEquals(BigDecimal.valueOf(-8).setScale(3, RoundingMode.HALF_EVEN), map.get(itemService.getItemById(8)));
    }

    @NotNull
    private ItemDoc getItemDoc(float quantity) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setDateTime(LocalDateTime.now());
        itemDoc.setDocType(DocumentType.WRITE_OFF_DOC);
        itemDoc.setStorageFrom(storageService.getById(3));
        Item item1 = itemService.getItemById(7);
        Item item2 = itemService.getItemById(8);
        Set<DocumentItem> items = Set.of(
                new DocumentItem(itemDoc, item1, BigDecimal.valueOf(quantity).setScale(3, RoundingMode.HALF_EVEN)),
                new DocumentItem(itemDoc, item2, BigDecimal.valueOf(quantity).setScale(3, RoundingMode.HALF_EVEN))
        );
        itemDoc.setDocumentItems(items);
        return itemDoc;
    }

    private Map<Lot, Float> getMapOfLotAndFloat() {
        Lot lot1 = new Lot(
                getDocument(1, 3), getItem(8),
                LocalDateTime.parse("2022-01-10T10:00:00"),
                10.00f, 50.00f);
        lot1.setId(1);
        Lot lot2 = new Lot(
                getDocument(2,3), getItem(8),
                LocalDateTime.parse("2022-01-20T10:00:00"),
                10.00f, 60.00f);
        Map<Lot, Float> map = new TreeMap<>();
        lot1.setId(2);
        map.put(lot1, 5.00f);
        map.put(lot2, 10.00f);
        return map;
    }

    private Map<Lot, BigDecimal> getMapOfLotAndBigDecimal() {
        Lot lot1 = new Lot(
                getDocument(1, 3), getItem(8),
                LocalDateTime.parse("2022-01-10T10:00:00"),
                10.00f, 50.00f);
        lot1.setId(1);
        Lot lot2 = new Lot(
                getDocument(2,3), getItem(8),
                LocalDateTime.parse("2022-01-20T10:00:00"),
                10.00f, 60.00f);
        Map<Lot, BigDecimal> map = new TreeMap<>();
        lot1.setId(2);
        map.put(lot1, BigDecimal.valueOf(5.00f));
        map.put(lot2, BigDecimal.valueOf(10.00f));
        return map;
    }

    private DocumentItem getDocItem(int id, float quantity) {
        DocumentItem item = new DocumentItem();
        item.setItem(getItem(id));
        item.setItemDoc(new ItemDoc());
        item.setQuantity(BigDecimal.valueOf(quantity));
        return item;
    }

    private ItemDoc getDocument(int id, int storageId) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setId(id);
        itemDoc.setStorageFrom(getStorage(storageId));
        return itemDoc;
    }

    private Item getItem(int id) {
        Item item = new Item();
        item.setId(id);
        return item;
    }

    private Storage getStorage(int id) {
        Storage storage = new Storage();
        storage.setId(id);
        storage.setName("Жаровня " + id);
        return storage;
    }
}