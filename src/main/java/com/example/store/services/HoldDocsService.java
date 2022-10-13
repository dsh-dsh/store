package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.enums.ExceptionType;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
                throw new BadRequestException(
                        Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE,
                        ExceptionType.UN_HOLD_EXCEPTION);
            }
        } else {
            if(documentRepository.existsByDateTimeBeforeAndIsDeletedAndIsHold(document.getDateTime(), false, false)) {
                throw new BadRequestException(
                        Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE,
                        ExceptionType.HOLD_EXCEPTION);
            }
        }
        return true;
    }

    @Transaction
    public void holdDoc(Document document) {
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
        if(document instanceof ItemDoc) {
            List<DocumentItem> items =
                    docItemService.getItemsByDoc((ItemDoc) document);
            lotMoveService.removeByDocument((ItemDoc) document);
            lotService.removeLotsForItems(items);
            itemDocRepository.save((ItemDoc) document);
        } else {
            orderDocRepository.save((OrderDoc) document);
        }
    }
}
