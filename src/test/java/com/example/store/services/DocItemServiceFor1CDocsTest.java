package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
class DocItemServiceFor1CDocsTest {

    @Autowired
    private DocItemServiceFor1CDocs service;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;

    @Sql(value = "/sql/hold1CDocs/addOneCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addDocItemTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(3611, 1, 180, 18);
        service.addDocItem(dto, doc);
        List<DocumentItem> items = docItemService.getItemsByDoc(doc);
        assertEquals(1, items.size());
        assertEquals(180, items.get(0).getPrice());
    }

    @Sql(value = "/sql/hold1CDocs/addOneCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void createDocItemTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(3611, 1, 180, 18);
        DocumentItem item = service.createDocItem(dto, doc);
        assertEquals(1, item.getQuantity());
        assertEquals(180, item.getPrice());
        assertEquals(18, item.getDiscount());
    }

    @Sql(value = "/sql/hold1CDocs/addOneCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void throwExceptionWhenCreateDocItemTest() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocItemDTO dto = getDocItemDTO(5, 1, 180, 18);
        BadRequestException exception = assertThrows(BadRequestException.class,
            () -> service.createDocItem(dto, doc));
        assertEquals(Constants.NO_SUCH_ITEM_MESSAGE, exception.getMessage());
    }

    public DocItemDTO getDocItemDTO(int itemId, float quantity, float price, float discount) {
        DocItemDTO dto = new DocItemDTO();
        dto.setItemId(itemId);
        dto.setQuantity(quantity);
        dto.setPrice(price);
        dto.setDiscount(discount);
        return dto;
    }
}