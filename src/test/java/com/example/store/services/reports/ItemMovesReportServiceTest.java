package com.example.store.services.reports;

import com.example.store.model.entities.Company;
import com.example.store.model.entities.Storage;
import com.example.store.model.reports.ItemLine;
import com.example.store.model.reports.MoveDocLine;
import com.example.store.services.CompanyService;
import com.example.store.services.StorageService;
import com.example.store.services.reports.ItemMovesReportService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class ItemMovesReportServiceTest {

    @Autowired
    private ItemMovesReportService itemMovesReportService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CompanyService companyService;

    @Test
    void getItemMoveReport() {
    }

    @Test
    void getReport() {
    }

    @Test
    void getItemLines() {
    }

    @Test
    void getItemLine() {
    }

    @Test
    void isNotNullFalse() {
        ItemLine line = getNewItemLine("Item name", 0, 0, 0, 0, null);
        itemMovesReportService.setIncludeNull(false);
        assertFalse(itemMovesReportService.isNotNull(line));
    }

    @Test
    void isNotNulTrueTrue() {
        ItemLine line = getNewItemLine("Item name", 10, 5, 3, 12, null);
        itemMovesReportService.setIncludeNull(true);
        assertTrue(itemMovesReportService.isNotNull(line));
    }

    @Test
    void isNotNulTrue() {
        ItemLine line = getNewItemLine("Item name", 10, 5, 3, 12, null);
        itemMovesReportService.setIncludeNull(false);
        assertTrue(itemMovesReportService.isNotNull(line));
    }

    @Test
    void isPostingReceipt() {
        DocumentItem documentItem = getDocItem(DocumentType.POSTING_DOC, null, null);
        assertTrue(itemMovesReportService.isReceipt(documentItem));
    }

    @Test
    void isReceipt() {
        DocumentItem documentItem = getDocItem(DocumentType.RECEIPT_DOC, null, null);
        assertTrue(itemMovesReportService.isReceipt(documentItem));
    }

    @Test
    void isMovementReceiptTrue() {
        Storage storageTo = storageService.getById(3);
        DocumentItem documentItem = getDocItem(DocumentType.MOVEMENT_DOC, null, storageTo);
        itemMovesReportService.setCurrentStorage(storageTo);
        assertTrue(itemMovesReportService.isReceipt(documentItem));
    }

    @Test
    void isMovementReceiptFalse() {
        Storage storageTo = storageService.getById(3);
        Storage currentStorage = storageService.getById(1);
        DocumentItem documentItem = getDocItem(DocumentType.MOVEMENT_DOC, null, storageTo);
        itemMovesReportService.setCurrentStorage(currentStorage);
        assertFalse(itemMovesReportService.isReceipt(documentItem));
    }

    @Test
    void getMoveDocLines() {
        Company supplier = companyService.getById(1);
        Storage currentStorage = storageService.getById(1);
        Storage storageTo = storageService.getById(3);
        List<DocumentItem> itemList = List.of(
                getDocItem(1, DocumentType.POSTING_DOC, supplier, null,
                        storageTo, LocalDateTime.now(), 123L, true),
                getDocItem(1, DocumentType.RECEIPT_DOC, null, null,
                        storageTo, LocalDateTime.now(), 123L, true),
                getDocItem(1, DocumentType.MOVEMENT_DOC, null, storageTo,
                        currentStorage, LocalDateTime.now(), 123L, true),
                getDocItem(1, DocumentType.WRITE_OFF_DOC, null, currentStorage,
                        null, LocalDateTime.now(), 123L, true)
        );
        itemMovesReportService.setCurrentStorage(currentStorage);
        List<MoveDocLine> lines = itemMovesReportService.getMoveDocLines(itemList);
        assertEquals(4, lines.size());
    }

    @Test
    void getMoveDocLinePosting() {
        LocalDateTime time = LocalDateTime.now();
        Company supplier = companyService.getById(1);
        Storage storageTo = storageService.getById(3);
        DocumentItem documentItem = getDocItem(
                1, DocumentType.POSTING_DOC, supplier, null, storageTo, time, 123L, true);
        MoveDocLine line = itemMovesReportService.getMoveDocLine(documentItem);
        assertEquals("ИП Шипилов М.В.", line.getSupplier());
        assertEquals("Жаровня 3", line.getStorageTo());
        assertEquals(0, line.getQuantity().compareTo(BigDecimal.TEN));
    }

    @Test
    void getMoveDocLineReceipt() {
        LocalDateTime time = LocalDateTime.now();
        Storage storageTo = storageService.getById(3);
        DocumentItem documentItem = getDocItem(
                1, DocumentType.RECEIPT_DOC, null, null, storageTo, time, 123L, true);
        itemMovesReportService.setCurrentStorage(storageTo);
        MoveDocLine line = itemMovesReportService.getMoveDocLine(documentItem);
        assertEquals("Жаровня 3", line.getStorageTo());
        assertEquals(0, line.getQuantity().compareTo(BigDecimal.TEN));
    }

    @Test
    void getMoveDocLineMovementFrom() {
        LocalDateTime time = LocalDateTime.now();
        Storage storageTo = storageService.getById(3);
        Storage storageFrom = storageService.getById(1);
        DocumentItem documentItem = getDocItem(
                1, DocumentType.MOVEMENT_DOC, null, storageFrom, storageTo, time, 123L, true);
        itemMovesReportService.setCurrentStorage(storageFrom);
        MoveDocLine line = itemMovesReportService.getMoveDocLine(documentItem);
        assertEquals("Склад", line.getSupplier());
        assertEquals("Жаровня 3", line.getStorageTo());
        assertEquals(0, line.getQuantity().compareTo(BigDecimal.TEN.negate()));
    }

    @Test
    void getMoveDocLineMovementTo() {
        LocalDateTime time = LocalDateTime.now();
        Storage storageTo = storageService.getById(3);
        Storage storageFrom = storageService.getById(1);
        DocumentItem documentItem = getDocItem(
                1, DocumentType.MOVEMENT_DOC, null, storageFrom, storageTo, time, 123L, true);
        itemMovesReportService.setCurrentStorage(storageTo);
        MoveDocLine line = itemMovesReportService.getMoveDocLine(documentItem);
        assertEquals("Склад", line.getSupplier());
        assertEquals("Жаровня 3", line.getStorageTo());
        assertEquals(0, line.getQuantity().compareTo(BigDecimal.TEN));
    }

    @Test
    void getMoveDocLineWriteOff() {
        LocalDateTime time = LocalDateTime.now();
        Storage storageFrom = storageService.getById(3);
        DocumentItem documentItem = getDocItem(
                1, DocumentType.WRITE_OFF_DOC, null, storageFrom, null, time, 123L, true);
        MoveDocLine line = itemMovesReportService.getMoveDocLine(documentItem);
        assertEquals("Жаровня 3", line.getSupplier());
        assertEquals(0, line.getQuantity().compareTo(BigDecimal.TEN.negate()));
        assertTrue(line.isHold());
    }

    @Test
    void isExpenseWriteOff() {
        DocumentItem documentItem = getDocItem(DocumentType.WRITE_OFF_DOC, null,null);
        assertTrue(itemMovesReportService.isExpense(documentItem));
    }

    @Test
    void isExpenseMovementTrue() {
        Storage storageFrom = storageService.getById(3);
        DocumentItem documentItem = getDocItem(DocumentType.MOVEMENT_DOC, storageFrom, null);
        itemMovesReportService.setCurrentStorage(storageFrom);
        assertTrue(itemMovesReportService.isExpense(documentItem));
    }

    @Test
    void isExpenseMovementFalse() {
        Storage storageFrom = storageService.getById(3);
        Storage currentStorage = storageService.getById(1);
        DocumentItem documentItem = getDocItem(DocumentType.MOVEMENT_DOC, storageFrom, null);
        itemMovesReportService.setCurrentStorage(currentStorage);
        assertFalse(itemMovesReportService.isExpense(documentItem));
    }

    @Test
    void isExpenseFalse() {
        DocumentItem documentItem = getDocItem(DocumentType.POSTING_DOC, null, null);
        assertFalse(itemMovesReportService.isExpense(documentItem));
    }

    @Test
    void getSupplierName() {
        Company company = companyService.getById(1);
        Storage storageTo = storageService.getById(3);
        ItemDoc doc = getItemDoc(company, null, storageTo);
        assertEquals("ИП Шипилов М.В.", itemMovesReportService.getSupplierName(doc));
    }

    @Test
    void getSupplierWhenEmptyThenReturnStorageName() {
        Storage storageFrom = storageService.getById(3);
        ItemDoc doc = getItemDoc(null, storageFrom, null);
        assertEquals("Жаровня 3", itemMovesReportService.getSupplierName(doc));
    }

    @Test
    void getStorageFromName() {
        Storage storageFrom = storageService.getById(3);
        ItemDoc doc = getItemDoc(null, storageFrom, null);
        assertEquals("Жаровня 3", itemMovesReportService.getStorageFromName(doc));
    }

    @Test
    void getEmptyStorageFromName() {
        Storage storageTo = storageService.getById(3);
        ItemDoc doc = getItemDoc(null,null, storageTo);
        assertEquals("", itemMovesReportService.getStorageFromName(doc));
    }

    @Test
    void getStorageToName() {
        Storage storageTo = storageService.getById(3);
        ItemDoc doc = getItemDoc(null, null, storageTo);
        assertEquals("Жаровня 3", itemMovesReportService.getStorageToName(doc));
    }

    @Test
    void getEmptyStorageToName() {
        Storage storageFrom = storageService.getById(3);
        ItemDoc doc = getItemDoc(null, storageFrom, null);
        assertEquals("", itemMovesReportService.getStorageToName(doc));
    }

    @Test
    void isWrightDocFalseType() {
        assertFalse(itemMovesReportService.isWrightDocType(
                getDocItem(DocumentType.CHECK_DOC, null, null)));
    }

    @Test
    void isWrightDocType() {
        assertTrue(itemMovesReportService.isWrightDocType(
                getDocItem(DocumentType.WRITE_OFF_DOC, null, null)));
    }

    private DocumentItem getDocItem(DocumentType docType, Storage storageFrom, Storage storageTo) {
        DocumentItem item = new DocumentItem();
        ItemDoc doc = new ItemDoc();
        doc.setDocType(docType);
        doc.setStorageFrom(storageFrom);
        doc.setStorageTo(storageTo);
        item.setItemDoc(doc);
        return item;
    }

    private DocumentItem getDocItem(
            int id, DocumentType docType, Company supplier, Storage storageFrom,
            Storage storageTo, LocalDateTime time, long number, boolean isHold) {
        DocumentItem item = new DocumentItem();
        ItemDoc doc = new ItemDoc();
        doc.setId(id);
        doc.setDocType(docType);
        doc.setNumber(number);
        doc.setSupplier(supplier);
        doc.setDateTime(time);
        doc.setStorageFrom(storageFrom);
        doc.setStorageTo(storageTo);
        doc.setHold(isHold);
        item.setItemDoc(doc);
        item.setQuantity(BigDecimal.TEN);
        return item;
    }

    private ItemDoc getItemDoc(Company supplier, Storage storageFrom, Storage storageTo) {
        ItemDoc doc = new ItemDoc();
        doc.setSupplier(supplier);
        doc.setStorageFrom(storageFrom);
        doc.setStorageTo(storageTo);
        return doc;
    }
     private ItemLine getNewItemLine(String name, float start, float add, float expense, float end, List<MoveDocLine> lines) {
        return new ItemLine(name,
                BigDecimal.valueOf(start),  BigDecimal.valueOf(add),
                BigDecimal.valueOf(expense), BigDecimal.valueOf(end), lines);
     }
}