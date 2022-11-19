package com.example.store.services;

import com.example.store.model.entities.Company;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.reports.ItemLine;
import com.example.store.model.reports.ItemMovesReport;
import com.example.store.model.reports.MoveDocLine;
import com.example.store.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ItemMovesReportService {

    @Autowired
    private StorageService storageService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private DocItemService docItemService;

    private Storage currentStorage;
    private LocalDateTime dateStart;
    private LocalDateTime dateEnd;
    private boolean includeNull;
    private boolean onlyHolden;

    // todo add tests

    public ItemMovesReport getItemMoveReport(int storageId, long start, long end, boolean includeNull, boolean onlyHolden) {
        this.currentStorage = storageService.getById(storageId);
        this.dateStart = Util.getLocalDateTime(start).plusSeconds(1); // plusSeconds(1) duy to not include close period docs
        this.dateEnd = Util.getLocalDateTime(end).plusDays(1).minusSeconds(1);
        this.includeNull = includeNull;
        this.onlyHolden = onlyHolden;
        return getReport();
    }

    public ItemMovesReport getReport() {
        Map<Item, BigDecimal> startRestList = itemRestService.getItemsRestOnStorage(currentStorage, dateStart);
        List<ItemLine> items = getItemLines(startRestList);
        return new ItemMovesReport(items);
    }

    private List<ItemLine> getItemLines(Map<Item, BigDecimal> startRestList) {
        return startRestList.entrySet().stream()
                .map(this::getItemLine)
                .filter(this::isNotNull)
                .sorted(Comparator.comparing(ItemLine::getName))
                .collect(Collectors.toList());
    }

    private boolean isNotNull(ItemLine itemLine) {
        if(includeNull) return true;
        return itemLine.getStartRest().compareTo(BigDecimal.ZERO) != 0
                || itemLine.getReceipt().compareTo(BigDecimal.ZERO) != 0
                || itemLine.getExpense().compareTo(BigDecimal.ZERO) != 0
                || itemLine.getEndRest().compareTo(BigDecimal.ZERO) != 0;
    }

    @NotNull
    private ItemLine getItemLine(Map.Entry<Item, BigDecimal> entry) {
        Item item = entry.getKey();
        List<DocumentItem> docItems = docItemService.getDocItemsByItem(item, currentStorage, dateStart, dateEnd, onlyHolden);
        BigDecimal startRest = entry.getValue();
        BigDecimal receiptAmount = docItems.stream()
                .filter(this::isReceipt).map(DocumentItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expenseAmount = docItems.stream()
                .filter(this::isExpense).map(DocumentItem::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal endRest = (startRest.add(receiptAmount)).subtract(expenseAmount);
        List<MoveDocLine> docLines = getMoveDocLines(docItems);
        return new ItemLine(item.getName(), startRest, receiptAmount, expenseAmount, endRest, docLines);
    }

    private boolean isReceipt(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        return (document.getDocType() == DocumentType.POSTING_DOC || document.getDocType() == DocumentType.RECEIPT_DOC)
                || (document.getDocType() == DocumentType.MOVEMENT_DOC && document.getStorageTo() == currentStorage);
    }

    private boolean isExpense(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        return (document.getDocType() == DocumentType.WRITE_OFF_DOC)
                || (document.getDocType() == DocumentType.MOVEMENT_DOC && document.getStorageFrom() == currentStorage);
    }

    @NotNull
    private List<MoveDocLine> getMoveDocLines(List<DocumentItem> docItems) {
        return docItems.stream()
                .filter(this::isWrightDocType)
                .map(this::getMoveDocLine)
                .collect(Collectors.toList());
    }

    private MoveDocLine getMoveDocLine(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        String date = Util.getDate(docItem.getItemDoc().getDateTime());
        String name = document.getDocType().getValue() + " № " + document.getNumber();
        String supplier = getSupplier(document);
        String storageTo = getStorageTo(document);
        boolean isExpense = (document.getDocType() == DocumentType.WRITE_OFF_DOC)
                || (document.getDocType() == DocumentType.MOVEMENT_DOC
                && document.getStorageFrom() == currentStorage);
        BigDecimal quantity = isExpense ? docItem.getQuantity().negate() : docItem.getQuantity();
        return new MoveDocLine(
                document.getId(), date, name, supplier,
                storageTo, quantity, document.isHold());
    }

    private String getSupplier(ItemDoc document) {
        Company supplier = document.getSupplier();
        String str = supplier == null? "" : supplier.getName();
        if(str.equals("")) {
            str = getStorageFrom(document);
        }
        return str;
    }

    private String getStorageFrom(ItemDoc document) {
        String storageFrom = "";
        Storage storage = document.getStorageFrom();
        storageFrom = storage != null ? storage.getName() : "";
        return storageFrom;
    }

    private String getStorageTo(ItemDoc document) {
        String storageTo = "";
        Storage storage = document.getStorageTo();
        storageTo = storage != null ? storage.getName() : "";
        return storageTo;
    }

    private boolean isWrightDocType(DocumentItem docItem) {
        Document document = docItem.getItemDoc();
        return document.getDocType() == DocumentType.WRITE_OFF_DOC
                || document.getDocType() == DocumentType.MOVEMENT_DOC
                || document.getDocType() == DocumentType.POSTING_DOC
                || document.getDocType() == DocumentType.RECEIPT_DOC;
    }
}
