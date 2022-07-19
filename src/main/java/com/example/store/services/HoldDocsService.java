package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.ExceptionType;
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
import org.springframework.data.domain.Sort;
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


    public boolean existsNotHoldenDocsBefore(Document document) {
        if(document.isHold()) {
            return false;
        }
        return documentRepository.existsByDateTimeLessThanAndIsDeletedAndIsHold(document.getDateTime(), false,false);
    }

    @Transaction
    public void holdDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        if(existsNotHoldenDocsBefore(document)) {
            throw new BadRequestException(
                    Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE,
                    ExceptionType.HOLD_EXCEPTION);
        }
        if(document.isHold()) {
            unHoldDoc(document);
        } else {
            holdDoc(document);
        }
    }

    // add tests
    public void serialHoldDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        List<Document> documents = documentRepository
                .findByIsHoldAndIsDeletedAndDateTimeBefore(false, false, document.getDateTime(), Sort.by(Constants.DATE_TIME_STRING));
        documents.add(document);
        documents.forEach(doc -> holdDocument(doc.getId()));
    }

    public void holdDoc(Document document) {
        document.setHold(true);
        if(document instanceof ItemDoc) {
            lotService.addLotMovements(document);
            itemDocRepository.save((ItemDoc) document);
        } else {
            orderDocRepository.save((OrderDoc) document);
        }
    }

    public void unHoldDoc(Document document) {
        document.setHold(false);
        if(document instanceof ItemDoc) {
            List<DocumentItem> items =
                    docItemService.getItemsByDoc((ItemDoc) document);
            lotMoveService.removeByDocument((ItemDoc) document);
            lotService.removeLots(items);
            itemDocRepository.save((ItemDoc) document);
        } else {
            orderDocRepository.save((OrderDoc) document);
        }
    }
}
