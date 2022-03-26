package com.example.store.factories.itemdoc;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.annotations.Transaction;
import org.springframework.stereotype.Component;

@Component
public class CheckDocFactory extends AbstractDocFactory {


    @Override
    @Transaction
    public DocInterface addDocument(DocDTO docDTO) {
        ItemDoc check = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(check);
        addCheckInfo(check);
        addDocumentItems(check);

        return check;
    }

    @Override
    @Transaction
    public DocInterface updateDocument(DocDTO docDTO) {
        ItemDoc check = getItemDoc(docDTO);
        setAdditionalFieldsAndSave(check);
        updateCheckInfo(check);
        updateDocItems(check);

        return check;
    }

    private void setAdditionalFieldsAndSave(ItemDoc check) {
        check.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        check.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        check.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
        itemDocRepository.save(check);
    }
}
