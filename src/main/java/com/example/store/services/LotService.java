package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.exceptions.NoDocumentItemsException;
import com.example.store.exceptions.UnHoldDocumentException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private IngredientService ingredientService;

    public static final Logger logger = LogManager.getLogger("LotService");

    public void addLotMovements(Document document) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc((ItemDoc) document);
        if(items.isEmpty()) throw new NoDocumentItemsException();
        DocumentType type = document.getDocType();
        if (type == DocumentType.RECEIPT_DOC || type == DocumentType.POSTING_DOC) {
            addLots(document);
        } else if (type == DocumentType.WRITE_OFF_DOC || type == DocumentType.MOVEMENT_DOC) {
            ingredientService.addInnerItems(items, document.getDateTime().toLocalDate());
            items.forEach(this::addStorageDocMovement);
        }
    }

    //TODO add test
    protected void addStorageDocMovement(DocumentItem docItem) {
        ItemDoc document = docItem.getItemDoc();
        Storage storage = document.getStorageFrom();
        LocalDateTime time = document.getDateTime();
        Map<Lot, Float> lotMap = getLotMap(docItem, storage, time);
        lotMoveService.addMinusMovements(document, lotMap);
        if(document.getDocType() == DocumentType.MOVEMENT_DOC) {
            lotMoveService.addPlusMovements(document, lotMap);
        }
    }

    public Map<Lot, Float> getLotMap(DocumentItem docItem, Storage storage, LocalDateTime time) {
        Map<Lot, Float> lotMap = getLotsOfItem(docItem.getItem(), storage, time);
        checkQuantityShortage(lotMap, docItem.getQuantity());
        return getLotMapToUse(lotMap, docItem.getQuantity());
    }

    public Map<Lot, Float> getLotsOfItem(Item item, Storage storage, LocalDateTime time) {
        List<LotFloat> lotsOfItem = lotRepository.getLotsOfItem(item.getId(), storage.getId(), time);
        return lotsOfItem.stream()
                .collect(Collectors.toMap(
                        lotFloat -> getLotById(lotFloat.getId()),
                        LotFloat::getValue,
                        (lf1, lf2) -> lf1,
                        TreeMap::new));
    }

    private Lot getLotById(long id) {
        return lotRepository.getById(id);
    }

    public void checkQuantityShortage(Map<Lot, Float> lotMap, float docItemQuantity) {
        double lotsQuantitySum = lotMap.values().stream().mapToDouble(d -> d).sum();
        if(docItemQuantity > lotsQuantitySum) throw new HoldDocumentException();
    }

    public Map<Lot, Float> getLotMapToUse(Map<Lot, Float> lotMap, float quantity) {
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

    //TODO add test
    public void addLots(Document document) {
        List<DocumentItem> items =
            docItemService.getItemsByDoc((ItemDoc) document);
        items.forEach(this::addLot);
    }

    //TODO add test
    private void addLot(DocumentItem item) {
        Lot lot = new Lot(item.getItemDoc(), item.getItem(),
                item.getItemDoc().getDateTime(), item.getQuantity(), item.getPrice());
        lotRepository.save(lot);
        lotMoveService.addPlusLotMovement(lot, item.getItemDoc(), item.getQuantity());
    }

    //TODO add test
    public void removeLots(List<DocumentItem> items) {
        items.forEach(this::removeLot);
    }

    //TODO add test
    private void removeLot(DocumentItem item) {
        Lot lot = lotRepository.findByDocument(item.getItemDoc())
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_LOT_MESSAGE));
        lotMoveService.removeLotMovement(lot);
        lotRepository.delete(lot);
    }

}
