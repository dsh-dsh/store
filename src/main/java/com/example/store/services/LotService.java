package com.example.store.services;

import com.example.store.components.EnvironmentVars;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.SettingType;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private EnvironmentVars env;
    @Autowired
    private List<PropertySetting> systemSettings;

    private boolean addRestForHold1CDocs = true;
    private boolean usingAveragePriceOfLots = true;
    private boolean is1CDoc = false;

    @PostConstruct
    protected void setSettings() {
        PropertySetting restSetting = PropertySetting.getByType(systemSettings, SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        if(restSetting != null) {
            addRestForHold1CDocs = restSetting.getProperty() == 1;
        }
        PropertySetting averagePriceSetting = PropertySetting.getByType(systemSettings, SettingType.DOCS_AVERAGE_PRICE);
        if(averagePriceSetting != null) {
            usingAveragePriceOfLots = averagePriceSetting.getProperty() == 1;
        }
    }

    public Lot getById(long id) {
        return lotRepository.getById(id);
    }

    public Lot getLotByDocumentItemForPosting(DocumentItem documentItem) {
        return lotRepository.findByDocumentItem(documentItem)
                    .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_LOT_MESSAGE));
    }

    public List<Lot> getLotsByDocumentItemForStorageDocs(DocumentItem docItem) {
        return lotRepository.findLotsByItemAndDoc(docItem.getItem().getId(), docItem.getItemDoc().getId());
    }

    public void updateLotMovements(ItemDoc itemDoc) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc(itemDoc);
        if(items.isEmpty()) throw new BadRequestException(Constants.NO_DOCUMENT_ITEMS_MESSAGE);
        updateLots(itemDoc);
    }

    public void updateLots(ItemDoc itemDoc) {
        List<DocumentItem> items = docItemService.getItemsByDoc(itemDoc);
        items.forEach(this::updateLot);
    }

    public void updateLot(DocumentItem item) {
        Lot lot = lotRepository.findByDocumentItem(item)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_LOT_MESSAGE));
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
        if(docItems.isEmpty()) throw new BadRequestException(Constants.NO_DOCUMENT_ITEMS_MESSAGE);
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

        Map<Lot, Float> lotMap = getLotMap(docItem, storage, docTime);
        lotMoveService.addMinusLotMovements(document, lotMap);

        setAveragePrice(docItem, lotMap);

        if(document.getDocType() == DocumentType.MOVEMENT_DOC) {
            lotMoveService.addPlusLotMovements(document, lotMap);
        }
    }

    protected void setAveragePrice(DocumentItem docItem, Map<Lot, Float> lotMap) {
        if(!usingAveragePriceOfLots) return;
        float averagePrice = ((float) lotMap.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getKey().getDocumentItem().getPrice() * entry.getValue())
                .sum()) / docItem.getQuantity();
        docItem.setPrice(Util.floorValue(averagePrice, 100));
    }

    public Map<Lot, Float> getLotMap(DocumentItem docItem, Storage storage, LocalDateTime endTime) {
        Map<Lot, Float> lotMap = getLotsOfItem(docItem.getItem(), storage, endTime);
        if(!is1CDoc || addRestForHold1CDocs) {
            itemRestService.checkQuantityShortage(lotMap, docItem.getQuantity());
        }
        return getLotMapToHold(lotMap, docItem.getQuantity());
    }

    public Map<Lot, Float> getLotsOfItem(Item item, Storage storage, LocalDateTime endTime) {
        List<LotFloat> lotsOfItem = lotRepository.getLotsOfItem(item.getId(), storage.getId(), env.getPeriodStart(), endTime);
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

    public Map<Lot, Float> getLotMapToHold(Map<Lot, Float> lotMap, float quantity) {
        Map<Lot, Float> newLotMap = new TreeMap<>();
        for(Map.Entry<Lot, Float> entry : lotMap.entrySet()) {
            if(quantity > 0) {
                float lotQuantity = quantity > entry.getValue()? entry.getValue() : quantity;
                newLotMap.put(entry.getKey(), lotQuantity);
                quantity = quantity - entry.getValue();
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

    public void removeLots(List<DocumentItem> items) {
        items.forEach(this::removeLot);
    }

    public void removeLot(DocumentItem docItem) {
        lotRepository.findByDocumentItem(docItem)
                .ifPresent(value -> lotRepository.delete(value));
    }

}
