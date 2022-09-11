package com.example.store.services;

import com.example.store.components.EnvironmentVars;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private EnvironmentVars env;

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
        env.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, Float> map = lotService.getLotMap(getDocItem(8, 2.00f), getStorage(3), endTime);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(2.00f)));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotMapThanTime20220311Test() {
        env.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, Float> map = lotService.getLotMap(getDocItem(8, 10.00f), getStorage(3), endTime);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
        assertThat(map, hasValue(equalTo(6.00f)));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getOneLotOfItemThanTime20220320Time() {
        env.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, Float> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), endTime);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getTwoLotsOfItemThanTime20220310Test() {
        env.setPeriodStart();
        LocalDateTime endTime = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, Float> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), endTime);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
        assertThat(map, hasValue(equalTo(10.00f)));
    }

    @Test
    void getLotMapOfOneToHoldTest() {
        Map<Lot, Float> map = lotService.getLotMapToHold(getMapOfLotAndFloat(), 5.00f);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(5.00f)));
    }

    @Test
    void getLotMapOfTwoToHoldTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        Map<Lot, Float> map = lotService.getLotMapToHold(mapOfLotAndFloat, 12.00f);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(5.00f)));
        assertThat(map, hasValue(equalTo(7.00f)));
    }

    @Test
    void checkQuantityShortageExceptionThrownTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        assertThrows(BadRequestException.class,
                () -> itemRestService.checkQuantityShortage(mapOfLotAndFloat, 20.00f));
    }

    @Sql(value = "/sql/lots/addTwoDocsAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfWriteOffDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(2);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(9, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageFrom(), LocalDateTime.now()));
        assertEquals(8, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageFrom(), LocalDateTime.now()));
    }

    @Sql(value = "/sql/lots/addReceiptDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfReceiptDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(5, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()));
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()));
    }

    @Sql(value = "/sql/lots/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfPostingDocTest() {
        ItemDoc document = (ItemDoc) documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(13, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()));
        assertEquals(8, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()));
    }

    @Sql(value = "/sql/lots/addPostingDocWithLotsAndMovementDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfMovementDocTest() {
        ItemDoc movementDocument = (ItemDoc) documentService.getDocumentById(2);
        lotService.addLotMovements(movementDocument);
        List<Item> items = movementDocument.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(4, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageFrom(), LocalDateTime.now()));
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageFrom(), LocalDateTime.now()));
        assertEquals(6, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageTo(), LocalDateTime.now()));
        assertEquals(7, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageTo(), LocalDateTime.now()));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addTwoDocsAndLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addStorageDocMovement_whenWriteOffDoc_Test() {
        env.setPeriodStart();
        DocumentItem item = docItemService.getItemById(3);
        lotService.addStorageDocMovement(item);
        assertEquals(1, lotService.getLotsByDocumentItemForStorageDocs(item).get(0).getId());
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lots/addPostingDocWithLotsAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql", "/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addStorageDocMovement_whenMovementDoc_Test() {
        env.setPeriodStart();
        DocumentItem item = docItemService.getItemById(3);
        lotService.addStorageDocMovement(item);
        List<Lot> lots = lotService.getLotsByDocumentItemForStorageDocs(item);
        assertEquals(1, lotService.getLotsByDocumentItemForStorageDocs(item).get(0).getId());
    }

    @Test
    void checkQuantityShortageNoExceptionTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        assertDoesNotThrow(
                () -> itemRestService.checkQuantityShortage(mapOfLotAndFloat, 15.00f));
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
        lotService.removeLots(documentItems);
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
        docItem.setQuantity(15.00f);
        Map<Lot, Float> lotMap = getMapOfLotAndFloat();
        lotService.setAveragePrice(docItem, lotMap);
        float expectedPrice = ((float)(50*5 + 60*10)) / 15;
        assertEquals(Util.floorValue(expectedPrice, 100), docItem.getPrice());
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

    private DocumentItem getDocItem(int id, float quantity) {
        DocumentItem item = new DocumentItem();
        item.setItem(getItem(id));
        item.setItemDoc(new ItemDoc());
        item.setQuantity(quantity);
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