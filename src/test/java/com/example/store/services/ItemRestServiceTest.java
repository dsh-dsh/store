package com.example.store.services;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ItemRestServiceTest {

    @Autowired
    private ItemRestService itemRestService;

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemRestMapTest() {
        Item item7 = new Item(7);
        Item item8 = new Item(8);
        Map<Item, Float> map = new HashMap<>();
        map.put(item7, 1f);
        map.put(item8, 2f);
        Map<Item, Float> items = itemRestService.getItemRestMap(map, new Storage(3), LocalDateTime.now());
        assertEquals(11, items.get(item7));
        assertEquals(11, items.get(item8));
        items = itemRestService.getItemRestMap(map, new Storage(2), LocalDateTime.now());
        assertEquals(2, items.get(item7));
        assertEquals(2, items.get(item8));
        items = itemRestService.getItemRestMap(map, new Storage(1), LocalDateTime.now());
        assertEquals(2, items.get(item7));
        assertEquals(2, items.get(item8));
    }

    @Sql(value = "/sql/lotMovements/addTwoPosting.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getLastPriceOfItemTest() {
        assertEquals(200, itemRestService.getLastPriceOfItem(new Item(7)));
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql",
            "/sql/lotMovements/addWriteOff.sql", "/sql/lotMovements/addMovement.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestOfItemOnStorageTest() {
        assertEquals(5, itemRestService.getRestOfItemOnStorage(new Item(7), new Storage(3), LocalDateTime.now()));
        assertEquals(3, itemRestService.getRestOfItemOnStorage(new Item(8), new Storage(3), LocalDateTime.now()));
        assertEquals(2, itemRestService.getRestOfItemOnStorage(new Item(7), new Storage(2), LocalDateTime.now()));
        assertEquals(2, itemRestService.getRestOfItemOnStorage(new Item(8), new Storage(1), LocalDateTime.now()));
    }

    @Sql(value = {"/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql",
            "/sql/lotMovements/addWriteOff.sql", "/sql/lotMovements/addMovement.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestOfLotTest() {
        assertEquals(5, itemRestService.getRestOfLot(lotWithId(1L), new Storage(3)));
        assertEquals(2, itemRestService.getRestOfLot(lotWithId(1L), new Storage(2)));
        assertEquals(3, itemRestService.getRestOfLot(lotWithId(2L), new Storage(3)));
        assertEquals(2, itemRestService.getRestOfLot(lotWithId(2L), new Storage(1)));
    }

    Lot lotWithId(long id){
        Lot lot = new Lot();
        lot.setId(id);
        return lot;
    }
}