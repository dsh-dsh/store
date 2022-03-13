package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class ReceiptDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface createDocument() {
        if (itemDocDTO == null) return null;
        ItemDoc postingDoc = getItemDoc();
        setDocumentType(DocumentType.RECEIPT_DOC);
        setCommonFields(postingDoc);
        setAdditionalFields(postingDoc);
        itemDocRepository.save(postingDoc);

        addDocItems(postingDoc);

        return postingDoc;
    }

    private void setAdditionalFields(ItemDoc postingDoc) {
        postingDoc.setSupplier(companyService.getById(itemDocDTO.getSupplier().getId()));
        postingDoc.setRecipient(companyService.getById(itemDocDTO.getRecipient().getId()));
        postingDoc.setStorageTo(storageService.getById(itemDocDTO.getStorageTo().getId()));
    }

}
