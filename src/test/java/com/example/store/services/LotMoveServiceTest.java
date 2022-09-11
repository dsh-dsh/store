package com.example.store.services;

import com.example.store.exceptions.UnHoldDocumentException;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class LotMoveServiceTest {

    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private LotService lotService;

    @Sql(value = "/sql/lotMovements/addDocAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addPlusLotMovementsTest() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Lot lot1 = lotService.getById(1);
        Lot lot2 = lotService.getById(2);
        Map<Lot, Float> map = new HashMap<>();
        map.put(lot1, 10f);
        map.put(lot2, 10f);
        lotMoveService.addPlusLotMovements(itemDoc, map);
        assertEquals(1, lotMoveRepository.findByLot(lot1).size());
        assertEquals(1, lotMoveRepository.findByLot(lot2).size());
    }

    @Sql(value = "/sql/lotMovements/addDocAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPlusLotMovementTest() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Lot lot = lotService.getById(1);
        lotMoveService.addPlusLotMovement(lot, itemDoc, 10f);
        assertEquals(1, lotMoveRepository.findByLot(lot).size());
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updatePlusLotMovement() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Lot lot = lotService.getById(1);
        lotMoveService.updatePlusLotMovement(lot, itemDoc, 15f);
        LotMovement lotMovement = lotMoveRepository.findByLot(lot).get(0);
        assertEquals(15, lotMovement.getQuantity());
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void addMinusLotMovementsTest() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Lot lot1 = lotService.getById(1);
        Lot lot2 = lotService.getById(2);
        Map<Lot, Float> map = new HashMap<>();
        map.put(lot1, -5f);
        map.put(lot2, -5f);
        lotMoveService.addPlusLotMovements(itemDoc, map);
        assertEquals(2, lotMoveRepository.findByLot(lot1).size());
        assertEquals(2, lotMoveRepository.findByLot(lot2).size());
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addMinusLotMovement() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Lot lot = lotService.getById(1);
        lotMoveService.addPlusLotMovement(lot, itemDoc, -5f);
        assertEquals(2, lotMoveRepository.findByLot(lot).size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getLotMovementsTest() {
        Lot lot = lotService.getById(1);
        assertEquals(6, lotMoveService.getLotMovements(lot).size());
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void removeLotMovementTest() {
        Lot lot = lotService.getById(1);
        lotMoveService.removeLotMovement(lot);
        assertEquals(0, lotMoveService.getLotMovements(lot).size());
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql", "/sql/lotMovements/addWriteOff.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void removeLotMovementThrowExceptionTest() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(3);
        Lot lot = lotService.getById(1);
        assertThrows(UnHoldDocumentException.class, () -> lotMoveService.removeLotMovement(lot));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void removeByDocumentTest() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(5);
        lotMoveService.removeByDocument(itemDoc);
        assertEquals(10, lotMoveService.getAll().size());
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAllTest() {
        assertEquals(14, lotMoveService.getAll().size());
    }

//    @Sql(value = "/sql/lotMovements/addLotsForCollisionTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void floatCollisionTest() {
        float lotQuantity = 10.0f;
        while(lotQuantity > 0) {
            float moveQuantity = (float) Math.floor(Math.random()*1000)/1000;
            lotQuantity = lotQuantity - Math.min(moveQuantity, lotQuantity);
            System.out.println(moveQuantity);
        }
        assertEquals(0, lotQuantity);
    }

}
