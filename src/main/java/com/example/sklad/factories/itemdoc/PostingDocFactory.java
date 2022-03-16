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
        this.docDTO = itemDocDTO;
        ItemDoc postingDoc = new ItemDoc();
        setDocumentType(DocumentType.POSTING_DOC);
        setCommonFields(postingDoc);
        setAdditionalFields(postingDoc);
        itemDocRepository.save(postingDoc);

        addDocItems(postingDoc);

        return postingDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc postingDoc = getItemDoc();
        updateCommonFields(postingDoc);
        setAdditionalFields(postingDoc);
        itemDocRepository.save(postingDoc);

        updateDocItems(postingDoc);

        return postingDoc;
    }

    private void setAdditionalFields(ItemDoc postingDoc) {
        postingDoc.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        postingDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
    }
}
