package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class RequestDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        ItemDoc requestDoc = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(requestDoc);
        addDocumentItems(requestDoc);

        return requestDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        ItemDoc requestDoc = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(requestDoc);
        updateDocItems(requestDoc);

        return requestDoc;
    }

    private void setAdditionalFieldsAndSave(ItemDoc requestDoc) {
        requestDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
        itemDocRepository.save(requestDoc);
    }
}
