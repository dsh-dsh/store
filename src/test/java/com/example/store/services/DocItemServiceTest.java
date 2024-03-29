package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocItemServiceTest {

    @Autowired
    private DocItemService docItemService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProjectService projectService;

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocItemTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(1, 5, 3f, 600f);
        docItemService.addDocItem(dto, doc);
        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(3, items.size());
        assertEquals(600, items.get(2).getPrice());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createDocItemTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(1, 5, 3f, 600f);
        DocumentItem item = docItemService.createDocItem(dto, doc);
        assertEquals(BigDecimal.valueOf(3.000).setScale(3, RoundingMode.CEILING), item.getQuantity());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createDocItemIfDocNotExistsTest() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> documentService.getDocumentById(1000));
        assertEquals(Constants.NO_SUCH_DOCUMENT_MESSAGE, exception.getMessage());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateDocItemsTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto1 = getDocItemDTO(1, 2, 2f, 300f);
        DocItemDTO dto2 = getDocItemDTO(1, 3, 3f, 500f);
        DocItemDTO dto3 = getDocItemDTO(1, 5, 3f, 600f);
        docItemService.updateDocItems(List.of(dto1, dto2, dto3), doc);
        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(3, items.size());
        assertEquals(300, items.get(0).getPrice());
        assertEquals(500, items.get(1).getPrice());
        assertEquals(600, items.get(2).getPrice());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateDocItemTest() {
        DocumentItem item = docItemService.getItemById(1);
        DocItemDTO dto = getDocItemDTO(1, 2, 2f, 300f);
        docItemService.updateDocItem(item, dto);
        item = docItemService.getItemById(1);
        assertEquals(BigDecimal.valueOf(2.000).setScale(3, RoundingMode.CEILING), item.getQuantity());
        assertEquals(300, item.getPrice());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemByIdTest() {
        DocumentItem item = docItemService.getItemById(1);
        assertNotNull(item);
        assertEquals(200, item.getPrice());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemByIdThenNotExistsTest() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> docItemService.getItemById(1000));
        assertEquals(Constants.NO_SUCH_DOCUMENT_ITEM_MESSAGE, exception.getMessage());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemsByDocTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(2, items.size());
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

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void deleteByDocTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        docItemService.deleteByDoc(doc);
        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(0, items.size());
    }

    @Sql(value = "/sql/documents/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void saveTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(1, 5, 3f, 600f);
        DocumentItem item = docItemService.createDocItem(dto, doc);
        docItemService.save(item);
        List<DocumentItem> list = docItemService.getItemsByDoc(doc);
        assertEquals(3, list.size());
    }

    @Sql(value = "/sql/documents/addTenChecks.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemsByPeriodTest() {
        Project project = projectService.getById(3);
        LocalDateTime start = Util.getLocalDate("16.04.22 00:00:00").atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<DocumentItem> list = docItemService.getItemsByPeriod(project, start, end, false);
        assertEquals(5, list.size());
    }

    public DocItemDTO getDocItemDTO(int docId, int itemId, float quantity, float price) {
        DocItemDTO dto = new DocItemDTO();
        dto.setDocumentId(docId);
        dto.setItemId(itemId);
        dto.setQuantity(quantity);
        dto.setPrice(price);
        return dto;
    }
}