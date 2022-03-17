package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.stereotype.Component;

@Component
public class PostingDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc postingDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(postingDoc);
        addDocumentItems(postingDoc);

        return postingDoc;
    }

    @Override
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
