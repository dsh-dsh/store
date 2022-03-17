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
        ItemDoc inventoryDoc = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(inventoryDoc);
        addDocumentItems(inventoryDoc);

        return inventoryDoc;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        ItemDoc inventoryDoc = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(inventoryDoc);
        updateDocItems(inventoryDoc);

        return inventoryDoc;
    }


    private void setAdditionalFieldsAndSave(ItemDoc inventoryDoc) {
        inventoryDoc.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        inventoryDoc.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        inventoryDoc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
        itemDocRepository.save(inventoryDoc);
    }
}
