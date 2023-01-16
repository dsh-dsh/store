package com.example.store.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AbstractDocCrudServiceTest {

    @Autowired
    private AbstractDocCrudService abstractDocCrudService;

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNextDocTimeWhenDocsExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        List<Integer> authorIds = abstractDocCrudService.getBlockingUserIds();
        abstractDocCrudService.setBlockingUserIds(List.of(6));
        assertEquals(LocalDateTime.parse("2022-03-16T11:30:36.396"), abstractDocCrudService.getNewDocTime(docDate, false));
        abstractDocCrudService.setBlockingUserIds(authorIds);
    }

    @Sql(value = "/sql/documents/add5DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getPreviousDocTimeWhenDocsExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        List<Integer> authorIds = abstractDocCrudService.getBlockingUserIds();
        abstractDocCrudService.setBlockingUserIds(List.of(6));
        assertEquals(LocalDateTime.parse("2022-03-16T06:30:36.394"), abstractDocCrudService.getNewDocTime(docDate, true));
        abstractDocCrudService.setBlockingUserIds(authorIds);
    }

    @Test
    void getNextDocTimeWhenDocsNotExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        List<Integer> authorIds = abstractDocCrudService.getBlockingUserIds();
        abstractDocCrudService.setBlockingUserIds(List.of(6));
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.000"), abstractDocCrudService.getNewDocTime(docDate, false));
        abstractDocCrudService.setBlockingUserIds(authorIds);
    }

    @Test
    void getPreviousDocTimeWhenDocsNotExistTest() {
        LocalDate docDate = LocalDate.parse("2022-03-16");
        List<Integer> authorIds = abstractDocCrudService.getBlockingUserIds();
        abstractDocCrudService.setBlockingUserIds(List.of(6));
        assertEquals(LocalDateTime.parse("2022-03-16T01:00:00.000"), abstractDocCrudService.getNewDocTime(docDate, true));
        abstractDocCrudService.setBlockingUserIds(authorIds);
    }
}
