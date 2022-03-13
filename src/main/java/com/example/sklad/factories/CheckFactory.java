package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class CheckFactory extends AbstractDocFactory {

    @Override
    public DocInterface createDocument() {
        if (itemDocDTO == null) return null;
        ItemDoc check = getItemDoc();
        setDocumentType(DocumentType.CHECK_DOC);
        setCommonFields(check);
        setAdditionalFields(check);
        itemDocRepository.save(check);

        addCheckInfo(check);
        addDocItems(check);

        return check;
    }

    private void setAdditionalFields(ItemDoc check) {
        check.setIndividual(userService.getById(itemDocDTO.getIndividual().getId()));
        check.setSupplier(companyService.getById(itemDocDTO.getSupplier().getId()));
        check.setStorageFrom(storageService.getById(itemDocDTO.getStorageFrom().getId()));
    }
}
