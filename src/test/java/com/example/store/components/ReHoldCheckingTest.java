package com.example.store.components;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.DocItemService;
import com.example.store.services.DocumentService;
import com.example.store.services.StorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ReHoldCheckingTest {

    @Autowired
    private ReHoldChecking reHoldChecking;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private StorageService storageService;


    private static final float REST_ON_STORAGE = 1f;
    private static final String DATE = "03.16.2022 14:00:00";

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
        DocDTO dto = getDocDTO(DATE,3, DocumentType.POSTING_DOC.toString());
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
        DocDTO dto = getDocDTO(DATE,3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 20f), getDocItemDTO(1,8, 20f));
        dto.setDocItems(items);
        assertTrue(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkPossibility_DecrementQuantities_Test() {
        ItemDoc doc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO(DATE,3, DocumentType.POSTING_DOC.toString());
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
        DocDTO dto = getDocDTO(DATE,3, DocumentType.POSTING_DOC.toString());
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
        DocDTO dto = getDocDTO(DATE,3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(
                getDocItemDTO(1,7, 15f));
        dto.setDocItems(items);
        assertFalse(reHoldChecking.checkPossibility(doc, dto));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getQuantityDiffMap() {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        Map<DocumentItem, Float> changedDocItemMap = new HashMap<>();
        DocumentItem documentItem1 = docItemService.getItemById(1);
        DocumentItem documentItem2 = docItemService.getItemById(2);
        changedDocItemMap.put(documentItem1, 2f);
        changedDocItemMap.put(documentItem2, 3f);

        Map<Item, Float> map = reHoldChecking.getQuantityDiffMap(itemDoc.getStorageTo(), changedDocItemMap);

        assertEquals(2, map.size());
        assertEquals(2 + REST_ON_STORAGE, map.get(docItemService.getItemById(1).getItem()));
        assertEquals(3 + REST_ON_STORAGE, map.get(docItemService.getItemById(2).getItem()));
    }

    @Sql(value = "/sql/lotMovements/addFewLotsAndMoves.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/lotMovements/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void setChangedDocItemsTest() {
        Map<DocumentItem, Float> changedDocItemMap = new HashMap<>();
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(1);
        DocDTO dto = getDocDTO("2022-03-16T14:00:00.000",3, DocumentType.POSTING_DOC.toString());
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 20f), getDocItemDTO(1,8, 20f));
        dto.setDocItems(items);
        reHoldChecking.setChangedDocItems(changedDocItemMap, itemDoc, dto);
        assertEquals(2, changedDocItemMap.size());
        assertEquals(10, changedDocItemMap.get(docItemService.getItemById(1)));
        assertEquals(10, changedDocItemMap.get(docItemService.getItemById(2)));
    }

    @Test
    void findDocItemDTOByItemIdTest() {
        List<DocItemDTO> items = List.of(getDocItemDTO(1,7, 20f), getDocItemDTO(1,8, 20f));
        assertTrue(reHoldChecking.findDocItemDTOByItemId(items,7).isPresent());
        assertEquals(7, reHoldChecking.findDocItemDTOByItemId(items,7).get().getItemId());
    }

    DocItemDTO getDocItemDTO(int docId, int itemId, float quantity){
        DocItemDTO dto = new DocItemDTO();
        dto.setDocumentId(docId);
        dto.setItemId(itemId);
        dto.setQuantity(quantity);
        return dto;
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
//        dto.setTime(time);
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