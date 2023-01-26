package com.example.store.services;

import com.example.store.exceptions.ShortageException;
import com.example.store.exceptions.WarningException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.ExceptionType;
import com.example.store.model.responses.ShortageResponseLine;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HoldDocsService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private LotService lotService;
    @Autowired
    private LotMoveService lotMoveService;

    public boolean checkPossibilityToHold(Document document) {
        if(document.isHold()) {
            if(documentRepository.existsByDateTimeAfterAndIsHold(document.getDateTime(), true)) {
                throw new WarningException(
                        Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE,
                        ExceptionType.UN_HOLD_EXCEPTION,
                        this.getClass().getName() + " - checkPossibilityToHold(Document document)");
            }
        } else {
            if(documentRepository.existsByDateTimeBeforeAndIsDeletedAndIsHold(document.getDateTime(), false, false)) {
                throw new WarningException(
                        Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE,
                        ExceptionType.HOLD_EXCEPTION,
                        this.getClass().getName() + " - checkPossibilityToHold(Document document)");
            }
        }
        return true;
    }

    @Transaction
    public void holdDoc(Document document) {
        if(document.isHold()) return;
        document.setHold(true);
        if(document instanceof ItemDoc) {
            lotService.addLotMovements(document);
            itemDocRepository.save((ItemDoc) document);
        } else {
            orderDocRepository.save((OrderDoc) document);
        }
    }

    public void hold1CDoc(ItemDoc itemDoc) {
        itemDoc.setHold(true);
        lotService.addLotMovementsFor1CDocs(itemDoc);
        itemDocRepository.save(itemDoc);
    }

    @Transaction
    public void unHoldDoc(Document document) {
        document.setHold(false);
        if(document.getDocType() == DocumentType.CREDIT_ORDER_DOC
                || document.getDocType() == DocumentType.WITHDRAW_ORDER_DOC) {
            orderDocRepository.save((OrderDoc) document);
        } else {
            List<DocumentItem> items =
                    docItemService.getItemsByDoc((ItemDoc) document);
            lotMoveService.removeByDocument((ItemDoc) document);
            lotService.removeLotsForItems(items);
            itemDocRepository.save((ItemDoc) document);
        }
    }

    public void checkDocItemQuantities(Document document) {
        if(document instanceof ItemDoc) {
            ItemDoc itemDoc = (ItemDoc) document;
            if(itemDoc.getDocType() != DocumentType.WRITE_OFF_DOC
                    && itemDoc.getDocType() != DocumentType.MOVEMENT_DOC) {
                return;
            }
            Set<DocumentItem> documentItems = itemDoc.getDocumentItems();
            if(documentItems.isEmpty()) {                                        // todo update tests due to this
                throw new WarningException(
                        Constants.NO_DOCUMENT_ITEMS_MESSAGE,
                        this.getClass().getName() + " - checkDocItemQuantities(Document document)");
            }
            Map<Item, BigDecimal> shortages = lotService.getShortageMapOfItems(
                    documentItems, itemDoc.getStorageFrom(), itemDoc.getDateTime());
            if(!shortages.isEmpty()) {
                List<ShortageResponseLine> list = shortages.entrySet().stream()
                        .map(entry -> new ShortageResponseLine(entry.getKey().getId(), entry.getKey().getName(), entry.getValue().abs(), entry.getKey().getUnit().getValue()))
                        .collect(Collectors.toList());
                throw new ShortageException(
                        String.format(Constants.SHORTAGE_OF_ITEMS_IN_DOC_MESSAGE,
                                document.getDocType().getValue(), document.getNumber()),
                        list,
                        document.getId(),
                        this.getClass().getName() + " - checkDocItemQuantities(Document document)");
            }
        }
    }
}
