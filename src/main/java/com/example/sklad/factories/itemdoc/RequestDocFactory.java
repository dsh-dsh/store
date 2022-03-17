package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.stereotype.Component;

@Component
public class RequestDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc requestDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(requestDoc);
        addDocumentItems(requestDoc);

        return requestDoc;
    }

    @Override
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
