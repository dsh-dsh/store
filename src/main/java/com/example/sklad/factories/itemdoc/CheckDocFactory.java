package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.stereotype.Component;

@Component
public class CheckDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        ItemDoc check = getItemDoc(itemDocDTO);
        setAdditionalFieldsAndSave(check);
        addCheckInfo(check);
        addDocumentItems(check);

        return check;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        ItemDoc check = getItemDoc(itemDocDTO);
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
