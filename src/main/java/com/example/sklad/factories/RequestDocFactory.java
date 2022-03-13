package com.example.sklad.factories;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class RequestDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        this.itemDocDTO = itemDocDTO;
        ItemDoc itemDoc = new ItemDoc();
        setDocumentType(DocumentType.REQUEST_DOC);
        setCommonFields(itemDoc);
        setAdditionalFields(itemDoc);
        itemDocRepository.save(itemDoc);

        addDocItems(itemDoc);

        return itemDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.itemDocDTO = itemDocDTO;
        ItemDoc itemDoc = getItemDoc();
        updateCommonFields(itemDoc);
        setAdditionalFields(itemDoc);
        itemDocRepository.save(itemDoc);

        updateDocItems(itemDoc);

        return itemDoc;
    }

    private void setAdditionalFields(ItemDoc itemDoc) {
        itemDoc.setStorageTo(storageService.getById(itemDocDTO.getStorageTo().getId()));
    }
}
