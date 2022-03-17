package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class PostingDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        ItemDoc postingDoc = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(postingDoc);
        addDocumentItems(postingDoc);

        return postingDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        ItemDoc postingDoc = getItemDoc(itemDocDTO);
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
