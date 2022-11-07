package com.example.store.services;

import com.example.store.components.PeriodStartDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.projections.LotBigDecimal;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private PeriodStartDateTime periodStartDateTime;
    @Autowired
    @Qualifier("addRestForHold")
    private PropertySetting addRestForHoldSetting;
    @Autowired
    @Qualifier("docsAveragePrice")
    private PropertySetting docsAveragePriceSetting;

    private boolean is1CDoc = false;

    public Lot getById(long id) {
        return lotRepository.getById(id);
    }

    public Lot getLotByDocumentItemForPosting(DocumentItem documentItem) {
        return lotRepository.findByDocumentItem(documentItem)
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_LOT_MESSAGE,
                            this.getClass().getName() + " - getLotByDocumentItemForPosting(DocumentItem documentItem)"));
    }

    public List<Lot> getLotsByDocumentItemForStorageDocs(DocumentItem docItem) {
        return lotRepository.findLotsByItemAndDoc(docItem.getItem().getId(), docItem.getItemDoc().getId());
    }

    public void updateLotMovements(ItemDoc itemDoc) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc(itemDoc);
        if(items.isEmpty()) {
            throw new BadRequestException(
                    Constants.NO_DOCUMENT_ITEMS_MESSAGE,
                    this.getClass().getName() + " - updateLotMovements(ItemDoc itemDoc)");
        }
        updateLots(itemDoc);
    }

    public void updateLots(ItemDoc itemDoc) {
        List<DocumentItem> items = docItemService.getItemsByDoc(itemDoc);
        items.forEach(this::updateLot);
    }

    public void updateLot(DocumentItem item) {
        Lot lot = lotRepository.findByDocumentItem(item)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_LOT_MESSAGE,
                        this.getClass().getName() + " - updateLot(DocumentItem item)"));
        lotMoveService.updatePlusLotMovement(lot, item.getItemDoc(), item.getQuantity());
    }

    public void addLotMovementsFor1CDocs(Document document) {
        this.is1CDoc = true;
        addLotMovements(document);
        this.is1CDoc = false;
    }

    public void addLotMovements(Document document) {
        List<DocumentItem> docItems =
                docItemService.getItemsByDoc((ItemDoc) document);
        if(docItems.isEmpty()) {
            throw new BadRequestException(
                    Constants.NO_DOCUMENT_ITEMS_MESSAGE,
                    this.getClass().getName() + " - addLotMovements(Document document)");
        }
        DocumentType type = document.getDocType();
        if (type == DocumentType.RECEIPT_DOC || type == DocumentType.POSTING_DOC || type == DocumentType.PERIOD_REST_MOVE_DOC) {
            addLots(document);
        } else if (type == DocumentType.WRITE_OFF_DOC || type == DocumentType.MOVEMENT_DOC) {
            ingredientService.addInnerItems(docItems, document.getDateTime().toLocalDate());
            docItems.forEach(this::addStorageDocMovement);
        }
    }

    protected void addStorageDocMovement(DocumentItem docItem){
        ItemDoc document = docItem.getItemDoc();
        Storage storage = document.getStorageFrom();
        LocalDateTime docTime = document.getDateTime();
        Map<Lot, BigDecimal> lotMap = getLotMap(docItem, storage, docTime);
        lotMoveService.addMinusLotMovements(document, lotMap);
        setAveragePrice(docItem, lotMap);
        if(document.getDocType() == DocumentType.MOVEMENT_DOC) {
            lotMoveService.addPlusLotMovements(document, lotMap);
        }
    }

    protected void setAveragePrice(DocumentItem docItem, Map<Lot, BigDecimal> lotMap) {
        if(docsAveragePriceSetting.getProperty() == 0) return;
        float averagePrice = ((float) lotMap.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getKey().getDocumentItem().getPrice() * entry.getValue().floatValue())
                .sum()) / docItem.getQuantity().floatValue();
        docItem.setPrice(Util.floorValue(averagePrice, 2));
    }

    public Map<Lot, BigDecimal> getLotMap(DocumentItem docItem, Storage storage, LocalDateTime endTime) {
        Item item = docItem.getItem();
        Map<Lot, BigDecimal> lotMap = getLotsOfItem(docItem.getItem(), storage, endTime);
        if(!is1CDoc || addRestForHoldSetting.getProperty() == 1) {
            itemRestService.checkQuantityShortage(item, lotMap, docItem.getQuantity());
        }
        return getLotMapToHold(lotMap, docItem.getQuantity());
    }

    public Map<Lot, BigDecimal> getLotsOfItem(Item item, Storage storage, LocalDateTime endTime) {
        List<LotBigDecimal> lotsOfItem = lotRepository.getLotsOfItem(item.getId(), storage.getId(), periodStartDateTime.get(), endTime);
        return lotsOfItem.stream()
                .collect(Collectors.toMap(
                        lotBigDecimal -> getLotById(lotBigDecimal.getId()),
                        LotBigDecimal::getValue,
                        (lf1, lf2) -> lf1,
                        TreeMap::new));
    }

    private Lot getLotById(long id) {
        return lotRepository.getById(id);
    }

    public Map<Lot, BigDecimal> getLotMapToHold(Map<Lot, BigDecimal> lotMap, BigDecimal quantity) {
        Map<Lot, BigDecimal> newLotMap = new TreeMap<>();
        for(Map.Entry<Lot, BigDecimal> entry : lotMap.entrySet()) {
            if(quantity.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal lotQuantity = quantity.compareTo(entry.getValue()) > 0? entry.getValue() : quantity;
                newLotMap.put(entry.getKey(), lotQuantity);
                quantity = quantity.subtract(entry.getValue());
            }
        }
        return newLotMap;
    }

    public void addLots(Document document) {
        List<DocumentItem> items = docItemService.getItemsByDoc((ItemDoc) document);
        items.forEach(this::addLot);
    }

    public void addLot(DocumentItem item) {
        Lot lot = new Lot(item, item.getItemDoc().getDateTime());
        lotRepository.save(lot);
        lotMoveService.addPlusLotMovement(lot, item.getItemDoc(), item.getQuantity());
    }

    public void removeLotsForItems(List<DocumentItem> items) {
        items.forEach(this::removeLot);
    }

    public void removeLot(DocumentItem docItem) {
        lotRepository.findByDocumentItem(docItem)
                .ifPresent(value -> lotRepository.delete(value));
    }

    public Map<Item, BigDecimal> getShortageMapOfItems(Set<DocumentItem> documentItems, Storage storage, LocalDateTime docTime) {
        return documentItems.stream()
                .collect(Collectors.toMap(DocumentItem::getItem, DocumentItem::getQuantity, BigDecimal::add))
                .entrySet().stream()
                .peek(entry -> entry.setValue(itemRestService.getRestOfItemOnStorage(entry.getKey(), storage, docTime).subtract(entry.getValue())))
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
