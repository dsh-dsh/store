package com.example.store.factories.itemdoc;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.annotations.Transaction;
import org.springframework.stereotype.Component;

@Component
public class InventoryDocFactory extends AbstractDocFactory {

    @Override
    @Transaction
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc inventoryDoc = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(inventoryDoc);
        addDocumentItems(inventoryDoc);

        return inventoryDoc;
    }

    @Override
    @Transaction
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc inventoryDoc = getItemDoc(docDTO);
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
