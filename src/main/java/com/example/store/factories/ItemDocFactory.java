package com.example.store.factories;

import com.example.store.components.UnHoldDocs;
import com.example.store.exceptions.BadRequestException;
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
import com.example.store.utils.Constants;
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

    @Override
    @Transaction
    public void deleteDocument(int docId) {
        ItemDoc itemDoc = itemDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
        unHoldDocs.unHoldAllDocsAfter(itemDoc);
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

    private void setAdditionalFieldsAndSave(ItemDoc itemDoc) {
        if(docDTO.getIndividual().getId() != 0) {
            itemDoc.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        }
        if(docDTO.getSupplier().getId() != 0) {
            itemDoc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        }
        if(docDTO.getRecipient().getId() != 0) {
            itemDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        }
        if(docDTO.getStorageTo().getId() != 0) {
            itemDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        }
        if(docDTO.getStorageFrom().getId() != 0) {
            itemDoc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
        }
        itemDocRepository.save(itemDoc);
    }

    @Override
    @Transaction
    public void unHoldDocument(Document document) {
        List<DocumentItem> items =
                docItemService.getItemsByDoc((ItemDoc) document);
        lotService.removeLots(items);
        document.setHold(false);
        itemDocRepository.save((ItemDoc) document);
    }
}
