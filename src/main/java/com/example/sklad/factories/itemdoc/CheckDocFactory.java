package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.utils.annotations.Transaction;
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
