package com.example.store.factories;

import com.example.store.components.UnHoldDocs;
import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.ItemRestService;
import com.example.store.services.LotService;
import com.example.store.components.ReHoldChecking;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemDocFactory extends AbstractDocFactory {

    @Autowired
    private LotService lotService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private ReHoldChecking reHoldChecking;
    @Autowired
    private UnHoldDocs unHoldDocs;

    @Override
    @Transaction
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc itemDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(itemDoc);
        addDocumentItems(itemDoc);
        if(docDTO.getDocType().equals(DocumentType.CHECK_DOC.getValue())) {
            addCheckInfo(itemDoc);
        }
        return itemDoc;
    }

    @Override
    @Transaction
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc itemDoc = getItemDoc(docDTO);
        boolean reHoldPossible = reHoldChecking.checkPossibility(itemDoc, docDTO);
        setAdditionalFieldsAndSave(itemDoc);
        updateDocItems(itemDoc);
        if(reHoldPossible) {
            lotService.updateLotMovements(itemDoc);
        } else {
            unHoldDocs.unHoldAllDocsAfter(itemDoc);
        }
        updateCheckInfo(itemDoc);
        return itemDoc;
    }

    private void setAdditionalFieldsAndSave(ItemDoc itemDoc) {
        if(docDTO.getIndividual() != null) {
            itemDoc.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        }
        if(docDTO.getIndividual() != null) {
            itemDoc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        }
        if(docDTO.getRecipient() != null) {
            itemDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        }
        if(docDTO.getStorageTo() != null) {
            itemDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        }
        if(docDTO.getStorageFrom() != null) {
            itemDoc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
        }
        itemDocRepository.save(itemDoc);
    }

    @Override
    @Transaction
    public void deleteDocument(int docId) {
        ItemDoc itemDoc = itemDocRepository.getById(docId);
        itemDoc.setDeleted(true);
        itemDocRepository.save(itemDoc);
    }

    @Override
    @Transaction
    public void holdDocument(Document document) {
        lotService.addLotMovements(document);
        document.setHold(true);
        itemDocRepository.save((ItemDoc) document);
    }

    @Override
    @Transaction
    @Deprecated(forRemoval = true)
    public void unHoldDocument(Document document) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc((ItemDoc) document);
        lotService.removeLots(items);
        document.setHold(false);
        itemDocRepository.save((ItemDoc) document);
    }
}
