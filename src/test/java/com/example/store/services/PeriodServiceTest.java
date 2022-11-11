package com.example.store.services;

import com.example.store.components.PeriodStartDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.PeriodDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Period;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PeriodServiceTest {

    @Autowired
    private PeriodService periodService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private PeriodStartDateTime periodStartDateTime;
    @Autowired
    private ItemService itemService;

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql",
            "/sql/period/addDeletedOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkPossibilityToClosePeriodTest() {
        assertDoesNotThrow(() -> periodService.checkPossibilityToClosePeriod());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql",
            "/sql/period/addNotHoldenOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void checkPossibilityToClosePeriodThrowsTest() {
        assertThrows(BadRequestException.class,
                () -> periodService.checkPossibilityToClosePeriod());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void closePeriodTest() {
        periodService.closePeriod();
        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.PERIOD_REST_MOVE_DOC);
        assertEquals(2, docs.size());
        assertEquals(LocalDateTime.parse("2022-05-01T00:00:00.000000"), docs.get(0).getDateTime());
        assertEquals(LocalDateTime.parse("2022-05-01T00:00:00.002000"), docs.get(1).getDateTime());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql",
            "/sql/period/addNotHoldenOrderDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void closePeriodThrowsTest() {
        assertThrows(BadRequestException.class, () -> periodService.closePeriod());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void closePeriodForStorage3Test() {
        Item frenchFries = itemService.getItemById(7);
        Item flour = itemService.getItemById(8);
        periodStartDateTime.setPeriodStart();
        LocalDateTime newPeriodStart = periodService.getNewPeriodStart();
        Storage storage = storageService.getById(3);
        periodService.closePeriodForStorage(newPeriodStart, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForClosingPeriod(storage, LocalDate.parse("2022-05-02").atStartOfDay());
        assertEquals(2, itemRestMap.size());
        assertEquals(12.0f, itemRestMap.get(frenchFries).getRest().floatValue());
        assertEquals(200.0f, itemRestMap.get(frenchFries).getPrice());
        assertEquals(14.0f, itemRestMap.get(flour).getRest().floatValue());
        assertEquals(100.0f, itemRestMap.get(flour).getPrice());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocItemsOnStorage1Test() {
        periodStartDateTime.setPeriodStart();
        LocalDateTime newPeriodStart = periodService.getNewPeriodStart();
        Storage storage = storageService.getById(1);
        ItemDoc doc = periodService.createRestMoveDoc(newPeriodStart, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForClosingPeriod(storage, doc.getDateTime());
        List<DocumentItem> items = periodService.getDocItems(doc, itemRestMap);
        assertEquals(2, items.size());
        assertEquals(BigDecimal.valueOf(3).setScale(3, RoundingMode.HALF_EVEN), items.get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(4).setScale(3, RoundingMode.HALF_EVEN), items.get(1).getQuantity());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/period/addHoldenReceiptDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocItemsOnStorage3Test() {
        periodStartDateTime.setPeriodStart();
        LocalDateTime newPeriodStart = periodService.getNewPeriodStart();
        Storage storage = storageService.getById(3);
        ItemDoc doc = periodService.createRestMoveDoc(newPeriodStart, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForClosingPeriod(storage, doc.getDateTime());
        DocumentItem[] items = periodService.getDocItems(doc, itemRestMap).toArray(new DocumentItem[0]);
        assertEquals(2, items.length);
        assertEquals(BigDecimal.valueOf(7).setScale(3, RoundingMode.HALF_EVEN), items[0].getQuantity());
        assertEquals(BigDecimal.valueOf(6).setScale(3, RoundingMode.HALF_EVEN), items[1].getQuantity());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createRestMoveDocTest() {
        LocalDateTime newPeriodStart = periodService.getNewPeriodStart();
        Storage storage = storageService.getById(3);
        ItemDoc doc = periodService.createRestMoveDoc(newPeriodStart, storage);
        assertEquals(LocalDate.parse("2022-05-01").atStartOfDay(), doc.getDateTime());
        assertEquals(1, doc.getNumber());
        assertEquals(DocumentType.PERIOD_REST_MOVE_DOC, doc.getDocType());
        assertEquals(storage, doc.getStorageTo());
        assertEquals(projectService.getById(4), doc.getProject());
        assertEquals(Constants.SYSTEM_USER_EMAIL, doc.getAuthor().getEmail());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCurrentPeriodTest() {
        Period period = periodService.getCurrentPeriod();
        assertEquals(LocalDate.parse("2022-04-01"), period.getStartDate());
    }

    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCurrentPeriodIfNotExistsTest() {
        assertEquals(LocalDate.parse(Constants.DEFAULT_PERIOD_START).minusDays(1), periodService.getCurrentPeriod().getEndDate());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNextPeriodTest() {
        Period period = periodService.setNextPeriod();
        assertEquals(LocalDate.parse("2022-05-31"), period.getEndDate());
        assertEquals(LocalDateTime.parse("2022-05-01T00:00"), periodStartDateTime.get());
    }

    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNextPeriodIfNoCurrentTest() {
        Period period = periodService.setNextPeriod();
        assertEquals(LocalDate.parse("2000-01-01"), period.getStartDate());
        assertEquals(LocalDateTime.parse("2000-01-01T00:00"), periodStartDateTime.get());
    }

    @Test
    void getPeriodDTOIfNoCurrentPeriodTest() {
        PeriodDTO dto = periodService.getPeriodDTO();
        assertEquals(946587600000L, dto.getEndDate());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void closePeriodManuallyTest() {
        PeriodDTO dto = periodService.closePeriodManually();
        assertEquals(1651352400000L, dto.getStartDate());
    }

    @Sql(value = "/sql/period/add7DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getBlockTimeIfExistsTest () {
        assertEquals(Util.getLongLocalDateTime("16.10.22 01:00:00") + 402L, periodService.getBlockTime());
    }

    @Test
    void getBlockTimeIfNotExistsTest () {
        assertEquals(Util.getLongLocalDateTime(
                LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay()),
                periodService.getBlockTime());
    }

}