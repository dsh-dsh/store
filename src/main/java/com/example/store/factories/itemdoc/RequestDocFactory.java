package com.example.store.factories.itemdoc;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.annotations.Transaction;
import org.springframework.stereotype.Component;

@Component
public class RequestDocFactory extends AbstractDocFactory {

    @Override
    @Transaction
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc requestDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(requestDoc);
        addDocumentItems(requestDoc);

        return requestDoc;
    }

    @Override
    @Transaction
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc requestDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(requestDoc);
        updateDocItems(requestDoc);

        return requestDoc;
    }

    private void setAdditionalFieldsAndSave(ItemDoc requestDoc) {
        requestDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        itemDocRepository.save(requestDoc);
    }
}
