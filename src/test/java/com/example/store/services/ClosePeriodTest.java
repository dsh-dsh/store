package com.example.store.services;

import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ClosePeriodTest {

    @Autowired
    private PeriodService periodService;
    @Autowired
    private DocumentService documentService;

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
}
