package com.example.store.factories.itemdoc;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.annotations.Transaction;
import org.springframework.stereotype.Component;

@Component
public class PostingDocFactory extends AbstractDocFactory {

    @Override
    @Transaction
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc postingDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(postingDoc);
        addDocumentItems(postingDoc);

        return postingDoc;
    }

    @Override
    @Transaction
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc postingDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(postingDoc);
        updateDocItems(postingDoc);

        return postingDoc;
    }

    private void setAdditionalFieldsAndSave(ItemDoc postingDoc) {
        postingDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        postingDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        itemDocRepository.save(postingDoc);
    }
}
