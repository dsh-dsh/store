package com.example.store.services;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.ItemDoc;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class LotServiceTest {

    @Autowired
    private LotService lotService;
    @Autowired
    private ItemRestService itemRestService;
//    @Autowired
//    private LotMoveRepository lotMoveRepository;
    @Autowired
    private DocumentService documentService;

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotMapThanTime20220320Test() {
        LocalDateTime time = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, Float> map = lotService.getLotMap(getDocItem(8, 2.00f), getStorage(3), time);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(2.00f)));
    }

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getLotMapThanTime20220311Test() {
        LocalDateTime time = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, Float> map = lotService.getLotMap(getDocItem(8, 10.00f), getStorage(3), time);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
        assertThat(map, hasValue(equalTo(6.00f)));
    }

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getOneLotOfItemThanTime20220320Time() {
        LocalDateTime time = LocalDateTime.parse("2022-03-20T11:00:00.000000");
        Map<Lot, Float> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), time);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
    }

    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getTwoLotsOfItemThanTime20220310Test() {
        LocalDateTime time = LocalDateTime.parse("2022-03-11T11:00:00.000000");
        Map<Lot, Float> map
                = lotService.getLotsOfItem(getItem(8), getStorage(3), time);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(4.00f)));
        assertThat(map, hasValue(equalTo(10.00f)));
    }

    @Test
    void getLotMapOfOneToUseTest() {
        Map<Lot, Float> map = lotService.getLotMapToUse(getMapOfLotAndFloat(), 5.00f);
        assertEquals(1, map.size());
        assertThat(map, hasValue(equalTo(5.00f)));
    }

    @Test
    void getLotMapOfTwoToUseTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        Map<Lot, Float> map = lotService.getLotMapToUse(mapOfLotAndFloat, 12.00f);
        assertEquals(2, map.size());
        assertThat(map, hasValue(equalTo(5.00f)));
        assertThat(map, hasValue(equalTo(7.00f)));
    }

    @Test
    void checkQuantityShortageExceptionThrownTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        assertThrows(HoldDocumentException.class, () -> {
            lotService.checkQuantityShortage(mapOfLotAndFloat, 20.00f);
        });
    }

    @Sql(value = "/sql/lots/addTwoDocsAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfWriteOffDocTest() {
        ItemDoc document = documentService.getDocumentById(2);
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
        ItemDoc document = documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()));
        assertEquals(5, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()));
    }

    @Sql(value = "/sql/lots/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfPostingDocTest() {
        ItemDoc document = documentService.getDocumentById(1);
        lotService.addLotMovements(document);
        List<Item> items = document.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(8, itemRestService.getRestOfItemOnStorage(items.get(0), document.getStorageTo(), LocalDateTime.now()));
        assertEquals(13, itemRestService.getRestOfItemOnStorage(items.get(1), document.getStorageTo(), LocalDateTime.now()));
    }

    @Sql(value = "/sql/lots/addPostingDocWithLotsAndMovementDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addLotMovementsOfMovementDocTest() {
        ItemDoc movementDocument = documentService.getDocumentById(2);
        lotService.addLotMovements(movementDocument);
        List<Item> items = movementDocument.getDocumentItems().stream().map(DocumentItem::getItem).collect(Collectors.toList());
        assertEquals(4, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageFrom(), LocalDateTime.now()));
        assertEquals(3, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageFrom(), LocalDateTime.now()));
        assertEquals(6, itemRestService.getRestOfItemOnStorage(items.get(0), movementDocument.getStorageTo(), LocalDateTime.now()));
        assertEquals(7, itemRestService.getRestOfItemOnStorage(items.get(1), movementDocument.getStorageTo(), LocalDateTime.now()));
    }

//    @Sql(value = {"/sql/lots/addDocs.sql", "/sql/lots/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/sql/lots/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void addMinusMovements() {
//        Map<Lot, Float> map = getMapOfLotAndFloat();
//        lotService.addMinusMovements(getDocument(1, 3), map);
//        List<LotMovement> list = lotMoveRepository.findByLot((Lot)((TreeMap)map).firstKey());
//        assertFalse(list.isEmpty());
//        list = lotMoveRepository.findByLot((Lot)((TreeMap)map).lastKey());
//        assertFalse(list.isEmpty());
//    }
//
//    @Test
//    void addPlusMovements() {
//    }

    @Test
    void checkQuantityShortageNoExceptionTest() {
        Map<Lot, Float> mapOfLotAndFloat = getMapOfLotAndFloat();
        assertDoesNotThrow(() -> {
            lotService.checkQuantityShortage(mapOfLotAndFloat, 15.00f);
        });
    }

    @NotNull
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

    @NotNull
    private DocumentItem getDocItem(int id, float quantity) {
        DocumentItem item = new DocumentItem();
        item.setItem(getItem(id));
        item.setItemDoc(new ItemDoc());
        item.setQuantity(quantity);
        return item;
    }

    @NotNull
    private ItemDoc getDocument(int id, int storageId) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setId(id);
        itemDoc.setStorageFrom(getStorage(storageId));
        return itemDoc;
    }

//    @NotNull
//    private List<LotFloat> getListOfLotFloatsOn20220311() {
//        LotFloat lotFloat1 = new LotFloat() {
//            public long getId() {
//                return 1L;
//            }
//            public float getValue() {
//                return 4.00f;
//            }
//        };
//        LotFloat lotFloat2 = new LotFloat() {
//            public long getId() {
//                return 2L;
//            }
//            public float getValue() {
//                return 10.00f;
//            }
//        };
//        List<LotFloat> list = List.of(lotFloat1, lotFloat2);
//        return list;
//    }

    @NotNull
    private Item getItem(int id) {
        Item item = new Item();
        item.setId(id);
        return item;
    }

    @NotNull
    private Storage getStorage(int id) {
        Storage storage = new Storage();
        storage.setId(id);
        storage.setName("Жаровня " + id);
        return storage;
    }
}