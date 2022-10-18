package com.example.store.services;

import com.example.store.components.PeriodStartDateTime;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.Storage;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ItemRestServiceTest {

    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private PeriodStartDateTime periodStartDateTime;
    @Autowired
    @Qualifier("periodAveragePrice")
    private PropertySetting periodAveragePriceSetting;

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenPostingDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getItemsRestOnStorageForPeriodTest() {
        periodStartDateTime.setPeriodStart();
        Storage storage = storageService.getById(3);
        Item item = itemService.getItemById(7);
        Map<Item, ItemRestService.RestPriceValue> map = itemRestService
                .getItemsRestOnStorageForClosingPeriod(storage, LocalDate.parse("2022-05-15").atStartOfDay());
        assertEquals(2, map.size());
        assertEquals(6, map.get(item).getRest());
        assertEquals(200, map.get(item).getPrice());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPeriodStartTest() {
        assertEquals(LocalDate.parse("2022-04-01").atStartOfDay(), itemRestService.getPeriodStartDateTime());
    }

    @Test
    void getPeriodStarDefaultTest() {
        assertEquals(LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay(), itemRestService.getPeriodStartDateTime());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addTwoPostingDocs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestAndPriceWhenLastPriceTest() {
        Item item = itemService.getItemById(7);
        Storage storage = storageService.getById(1);

        int currentAveragePriceSetting = periodAveragePriceSetting.getProperty();
        periodAveragePriceSetting.setProperty(0);

        ItemRestService.RestPriceValue value = itemRestService
                .getRestAndPriceForClosingPeriod(item, storage, LocalDate.parse("2022-05-15").atStartOfDay());

        periodAveragePriceSetting.setProperty(currentAveragePriceSetting);

        assertEquals(2, value.getRest());
        assertEquals(200, value.getPrice());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addTwoPostingDocs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestAndPriceWhenAveragePriceTest() {
        Item item = itemService.getItemById(7);
        Storage storage = storageService.getById(1);

        int currentAveragePriceSetting = periodAveragePriceSetting.getProperty();
        periodAveragePriceSetting.setProperty(1);

        ItemRestService.RestPriceValue value = itemRestService
                .getRestAndPriceForClosingPeriod(item, storage, LocalDate.parse("2022-05-15").atStartOfDay());

        periodAveragePriceSetting.setProperty(currentAveragePriceSetting);

        assertEquals(2, value.getRest());
        assertEquals(150, value.getPrice());
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql",
            "/sql/lotMovements/addTwoPosting.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAveragePriceOfItemTest() {
        Item item = itemService.getItemById(7);
        Storage storage = storageService.getById(3);
        float actualPrice = itemRestService.getAveragePriceOfItem(item, storage, LocalDateTime.now(), 20);
        assertEquals(150.00f, actualPrice);
    }

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
        assertEquals(200, itemRestService.getLastPriceOfItem(new Item(7), LocalDateTime.now()));
    }

    @Sql(value = {"/sql/period/addPeriodMarch.sql", "/sql/lotMovements/addDocAndLots.sql", "/sql/lotMovements/addMoves.sql",
            "/sql/lotMovements/addWriteOff.sql", "/sql/lotMovements/addMovement.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestOfItemOnStorageTest() {
        periodStartDateTime.setPeriodStart();
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