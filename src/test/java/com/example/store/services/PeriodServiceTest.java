package com.example.store.services;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Period;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
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

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql",
            "/sql/period/addHoldenPostingDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void closePeriodTest() {
        periodService.closePeriod();
        List<ItemDoc> docs = documentService.getItemDocsByType(DocumentType.PERIOD_REST_MOVE_DOC);
        assertEquals(2, docs.size());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql",
            "/sql/period/addHoldenPostingDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void closePeriodForStorage3Test() {
        Period period = periodService.setNextPeriod();
        Storage storage = storageService.getById(3);
        periodService.closePeriodForStorage(period, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForPeriod(storage, LocalDate.parse("2022-05-02").atStartOfDay());
        assertEquals(2, itemRestMap.size());
        // todo assert quantity (after period close)
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql",
            "/sql/period/addHoldenPostingDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocItemsOnStorage1Test() {
        Period period = periodService.setNextPeriod();
        Storage storage = storageService.getById(1);
        ItemDoc doc = periodService.createRestMoveDoc(period, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForPeriod(storage, doc.getDateTime());
        List<DocumentItem> items = periodService.getDocItems(doc, itemRestMap);
        assertEquals(2, items.size());
        assertEquals(4, items.get(0).getQuantity());
        assertEquals(3, items.get(1).getQuantity());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql",
            "/sql/period/addHoldenPostingDocAndMovementDoc.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocItemsOnStorage3Test() {
        Period period = periodService.setNextPeriod();
        Storage storage = storageService.getById(3);
        ItemDoc doc = periodService.createRestMoveDoc(period, storage);
        Map<Item, ItemRestService.RestPriceValue> itemRestMap
                = itemRestService.getItemsRestOnStorageForPeriod(storage, doc.getDateTime());
        DocumentItem[] items = periodService.getDocItems(doc, itemRestMap).toArray(new DocumentItem[0]);
        assertEquals(2, items.length);
        assertEquals(6, items[0].getQuantity());
        assertEquals(7, items[1].getQuantity());
    }

    @Sql(value = {"/sql/period/addPeriods.sql",
            "/sql/hold1CDocs/addSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/period/after.sql",
            "/sql/hold1CDocs/deleteSystemUser.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createRestMoveDocTest() {
        Period period = periodService.setNextPeriod();
        Storage storage = storageService.getById(3);
        ItemDoc doc = periodService.createRestMoveDoc(period, storage);
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
        assertNull(periodService.getCurrentPeriod());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNextPeriodTest() {
        Period period = periodService.setNextPeriod();
        assertEquals(LocalDate.parse("2022-05-31"), period.getEndDate());
    }

    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setNextPeriodIfNoCurrentTest() {
        Period period = periodService.setNextPeriod();
        assertEquals(LocalDate.now(), period.getStartDate());
    }
}