package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class InventoryDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc inventoryDoc = new ItemDoc();
        setDocumentType(DocumentType.INVENTORY_DOC);
        setCommonFields(inventoryDoc);
        setAdditionalFields(inventoryDoc);
        itemDocRepository.save(inventoryDoc);

        addDocItems(inventoryDoc);

        return inventoryDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc inventoryDoc = getItemDoc();
        updateCommonFields(inventoryDoc);
        setAdditionalFields(inventoryDoc);
        itemDocRepository.save(inventoryDoc);

        updateDocItems(inventoryDoc);

        return inventoryDoc;
    }


    private void setAdditionalFields(ItemDoc doc) {
        doc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        doc.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        doc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
    }
}
