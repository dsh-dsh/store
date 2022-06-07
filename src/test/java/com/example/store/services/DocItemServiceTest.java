package com.example.store.services;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.documents.ItemDoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class DocItemServiceTest {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private DocumentService documentService;

    @Test
    void addDocItemTest() {
    }

    @Test
    void createNewDocItemTest() {
    }

    @Test
    void createDocItemTest() {
    }

    @Test
    void updateDocItemsTest() {
    }

    @Test
    void updateDocItemTest() {
    }

    @Test
    void getItemByIdTest() {
    }

    @Test
    void getItemsByDocTest() {
    }


    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getItemDTOListByDocTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        List<DocItemDTO> list = docItemService.getItemDTOListByDoc(doc);
        assertEquals(2, list.size());
    }

    @Test
    void deleteByDocTest() {
    }

    @Test
    void saveTest() {
    }
}