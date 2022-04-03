package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private DocItemService docItemService;

    public void addLotMovements(Document document) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc((ItemDoc) document);
        switch (document.getDocType()) {
            case RECEIPT_DOC:
            case POSTING_DOC:
                addLots(document);
                break;
            case WRITE_OFF_DOC:
                items.forEach(this::addWriteOffDocMovement);
                break;
            case MOVEMENT_DOC:
                items.forEach(this::addMoveDocMovement);
                break;
        }
    }

    private void addWriteOffDocMovement(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        Storage storage = document.getStorageFrom();
        LocalDateTime time = document.getDateTime();
        Map<Lot, Float> lotMap = getLotMap(docItem, storage, time);
        addMinusMovements(document, lotMap);
    }

    private void addMoveDocMovement(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        Storage storage = document.getStorageFrom();
        LocalDateTime time = document.getDateTime();
        Map<Lot, Float> lotMap = getLotMap(docItem, storage, time);
        addMinusMovements(document, lotMap);
        addPlusMovements(document, lotMap);
    }

    private Map<Lot, Float> getLotMap(DocumentItem docItem, Storage storage, LocalDateTime time) {
        Map<Lot, Float> lotMap = getLotsOfItem(docItem.getItem(), storage, time);
        double itemQuantitySum = lotMap.values().stream().mapToDouble(d -> d).sum();
        float quantity = docItem.getQuantity();
        if(quantity > itemQuantitySum) throw new HoldDocumentException();
        return getLotMapToUse(lotMap, quantity);
    }

    private Map<Lot, Float> getLotsOfItem(Item item, Storage storage, LocalDateTime time) {
        Collection<LotFloat> collection = lotRepository.getLotsOfItem(item.getId(), storage.getId(), time);
        return collection.stream()
                .collect(Collectors.toMap(LotFloat::getLot, LotFloat::getValue, (lf1, lf2) -> lf1, TreeMap::new));
    }

    private Map<Lot, Float> getLotMapToUse(Map<Lot, Float> lotMap, float quantity) {
        Map<Lot, Float> newLotMap = new TreeMap<>();
        for(Map.Entry<Lot, Float> entry : lotMap.entrySet()) {
            if(quantity > 0){
                float lotQuantity = quantity > entry.getValue()? entry.getValue() : quantity;
                newLotMap.put(entry.getKey(), lotQuantity);
                quantity = quantity - entry.getValue();
            }
        }
        return newLotMap;
    }

    private void addPlusMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.entrySet()
                .forEach(entry -> lotMoveService
                        .addMinusLotMovement(entry.getKey(), document, entry.getValue()));
    }

    private void addMinusMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.entrySet()
                .forEach(entry -> lotMoveService
                        .addPlusLotMovement(entry.getKey(), document, entry.getValue()));
    }

    private void addLots(Document document) {
        List<DocumentItem> items =
            docItemService.getItemsByDoc((ItemDoc) document);
        items.forEach(this::addLot);
    }

    private void addLot(DocumentItem item) {
        Lot lot = new Lot(item.getItemDoc(), item.getItem(),
                LocalDateTime.now(), item.getQuantity(), item.getPrice());
        lotMoveService.addPlusLotMovement(lot, item);
        lotRepository.save(lot);
    }

    public void removeLots(List<DocumentItem> items) {
        items.forEach(this::removeLot);
    }

    private void removeLot(DocumentItem item) {
        Lot lot = lotRepository.findByDocument(item.getItemDoc())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_LOT_MESSAGE));
        lotMoveService.removeLotMovement(lot);
        lotRepository.delete(lot);
    }

}