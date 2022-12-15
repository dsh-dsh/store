package com.example.store.services;

import com.example.store.components.PeriodDateTime;
import com.example.store.components.SystemSettingsCash;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.Storage;
import com.example.store.model.enums.SettingType;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private PeriodDateTime periodDateTime;
    @Autowired
    private SystemSettingsCash systemSettingsCash;

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getItemsRestOnStorageForPeriodTest() {
        periodDateTime.setPeriodStart();
        Storage storage = storageService.getById(3);
        Item item = itemService.getItemById(7);
        Map<Item, ItemRestService.RestPriceValue> map = itemRestService
                .getItemsRestOnStorageForClosingPeriod(storage, LocalDate.parse("2022-05-15").atStartOfDay());
        assertEquals(2, map.size());
        assertEquals(6, map.get(item).getRest().floatValue());
        assertEquals(200, map.get(item).getPrice());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPeriodStartTest() {
        assertEquals(LocalDate.parse("2022-04-01").atStartOfDay(), itemRestService.getPeriodDateTime());
    }

    @Test
    void getPeriodStarDefaultTest() {
        assertEquals(LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay(), itemRestService.getPeriodDateTime());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addTwoPostingDocs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestAndPriceWhenLastPriceTest() {
        Item item = itemService.getItemById(7);
        Storage storage = storageService.getById(1);

        int currentAveragePriceSetting = systemSettingsCash.getProperty(SettingType.PERIOD_AVERAGE_PRICE);
        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE, 2);

        ItemRestService.RestPriceValue value = itemRestService
                .getRestAndPriceForClosingPeriod(item, storage, LocalDate.parse("2022-05-15").atStartOfDay());

        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE, currentAveragePriceSetting);

        assertEquals(2, value.getRest().floatValue());
        assertEquals(200, value.getPrice());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addTwoPostingDocs.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getRestAndPriceWhenAveragePriceTest() {
        periodDateTime.setPeriodStart();

        Item item = itemService.getItemById(7);
        Storage storage = storageService.getById(1);

        int currentAveragePriceSetting = systemSettingsCash.getProperty(SettingType.PERIOD_AVERAGE_PRICE);
        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE, 1);

        ItemRestService.RestPriceValue value = itemRestService
                .getRestAndPriceForClosingPeriod(item, storage, LocalDate.parse("2022-05-15").atStartOfDay());

        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE, currentAveragePriceSetting);

        assertEquals(2, value.getRest().floatValue());
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
    @Transactional
    void getItemRestMapTest() {
        periodDateTime.setPeriodStart();
        Item item7 = itemService.getItemById(7);
        Item item8 = itemService.getItemById(8);
        List<Item> itemList = List.of(item7, item8);
        Map<Item, BigDecimal> items = itemRestService.getItemRestMap(itemList, new Storage(3), LocalDateTime.now());
        assertEquals(11, items.get(item7).floatValue());
        assertEquals(11, items.get(item8).floatValue());
        items = itemRestService.getItemRestMap(itemList, new Storage(2), LocalDateTime.now());
        assertEquals(2, items.get(item7).floatValue());
        assertEquals(2, items.get(item8).floatValue());
        items = itemRestService.getItemRestMap(itemList, new Storage(1), LocalDateTime.now());
        assertEquals(2, items.get(item7).floatValue());
        assertEquals(2, items.get(item8).floatValue());
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
        periodDateTime.setPeriodStart();
        assertEquals(5, itemRestService.getRestOfItemOnStorage(new Item(7), new Storage(3), LocalDateTime.now()).floatValue());
        assertEquals(3, itemRestService.getRestOfItemOnStorage(new Item(8), new Storage(3), LocalDateTime.now()).floatValue());
        assertEquals(2, itemRestService.getRestOfItemOnStorage(new Item(7), new Storage(2), LocalDateTime.now()).floatValue());
        assertEquals(2, itemRestService.getRestOfItemOnStorage(new Item(8), new Storage(1), LocalDateTime.now()).floatValue());
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

    @Sql(value = "/sql/lotMovements/addTwoPosting.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemRestTest() {
        List<DocItemDTO> list = itemRestService.getItemRest(1, Util.getLongLocalDateTime(LocalDateTime.now()), 3);
        assertEquals(1, list.size());
        assertEquals(20.0f, list.get(0).getQuantity());
    }

    Lot lotWithId(long id){
        Lot lot = new Lot();
        lot.setId(id);
        return lot;
    }
}