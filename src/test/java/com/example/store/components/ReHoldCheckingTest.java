package com.example.store.components;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ReHoldCheckingTest {

    @Autowired
    private ReHoldChecking reHoldChecking;
    @Autowired
    private DocumentService documentService;

    @Test
    void checkPossibility_IfDocNotHolden_Test() {
        ItemDoc doc = new ItemDoc();
        assertFalse(reHoldChecking.checkPossibility(doc, new DocDTO()));
    }

    @Test
    void checkPossibility_IfStorageNotEquals_Test() {
        LocalDateTime time = LocalDateTime.now();
        ItemDoc doc = getItemDoc(time, 1, DocumentType.POSTING_DOC);
        DocDTO dto = getDocDTO(time.toString(),3, DocumentType.POSTING_DOC.toString());
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Test
    void checkPossibility_IfTimeNotEquals_Test() {
        LocalDateTime time = LocalDateTime.now();
        ItemDoc doc = getItemDoc(time, 3, DocumentType.POSTING_DOC);
        DocDTO dto = getDocDTO("2022-03-16T18:00:00.000",3, DocumentType.POSTING_DOC.toString());
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Test
    void checkPossibility_IfDocTypeWriteOff_Test() {
        LocalDateTime time = LocalDateTime.now();
        ItemDoc doc = getItemDoc(time, 3, DocumentType.WRITE_OFF_DOC);
        DocDTO dto = getDocDTO(time.toString(),3, DocumentType.WRITE_OFF_DOC.toString());
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_QuantitiesDidNotChanged_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 10f), getDocItemDTO(1,8, 10f));
        dto.setDocItems(items);
        assertTrue(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_IncrementQuantities_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 20f), getDocItemDTO(1,8, 20f));
        dto.setDocItems(items);
        assertTrue(reHoldChecking.checkPossibility(doc, dto));
    }

    DocItemDTO getDocItemDTO(int docId, int itemId, float quantity){
        DocItemDTO dto = new DocItemDTO();
        dto.setDocumentId(docId);
        dto.setItemId(itemId);
        dto.setQuantity(quantity);
        return dto;
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_DecrementQuantities_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 5f), getDocItemDTO(1,8, 5f));
        dto.setDocItems(items);
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_AddItem_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(
                getDocItemDTO(1,7, 15f),
                getDocItemDTO(1,8, 15f),
                getDocItemDTO(1,1, 15f));
        dto.setDocItems(items);
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_DeleteItem_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(
                getDocItemDTO(1,7, 15f));
        dto.setDocItems(items);
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Test
    void getQuantityDiffMap() {
    }

    @Test
    void setQuantityDiffMap() {
    }

    @Test
    void setChangedDocItems() {
    }

    @Test
    void findDocItemDTOByItemId() {
    }

    private ItemDoc getItemDoc(LocalDateTime time, int storageId, DocumentType type) {
        ItemDoc doc = new ItemDoc();
        doc.setDateTime(time);
        doc.setStorageTo(new Storage(storageId));
        doc.setDocType(type);
        return doc;
    }

    private DocDTO getDocDTO(String time, int storageId, String type) {
        DocDTO dto = new DocDTO();
        dto.setTime(time);
        dto.setStorageTo(getStorageDTO(storageId));
        dto.setDocType(type);
        return dto;
    }

    private StorageDTO getStorageDTO(int storageId) {
        StorageDTO storageDTO = new StorageDTO();
        storageDTO.setId(storageId);
        return storageDTO;
    }
}