package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class PostingDocFactory extends AbstractDocFactory{

    @Override
    public DocInterface addDocument() {
        if (itemDocDTO == null) return null;
        ItemDoc postingDoc = getItemDoc();
        setDocumentType(DocumentType.POSTING_DOC);
        setCommonFields(postingDoc);
        setAdditionalFields(postingDoc);
        itemDocRepository.save(postingDoc);

        addDocItems(postingDoc);

        return postingDoc;
    }

    @Override
    public DocInterface updateDocument() {
        return null;
    }

    private void setAdditionalFields(ItemDoc postingDoc) {
        postingDoc.setRecipient(companyService.getById(itemDocDTO.getRecipient().getId()));
        postingDoc.setStorageTo(storageService.getById(itemDocDTO.getStorageTo().getId()));
    }
}
