package com.example.sklad.factories.itemdoc;

import com.example.sklad.factories.abstraction.AbstractDocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class CheckDocFactory extends AbstractDocFactory {

    @Override
    public DocInterface addDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc check = new ItemDoc();
        setDocumentType(DocumentType.CHECK_DOC);
        setCommonFields(check);
        setAdditionalFields(check);
        itemDocRepository.save(check);

        addCheckInfo(check);
        addDocItems(check);

        return check;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc check = getItemDoc();
        updateCommonFields(check);
        setAdditionalFields(check);
        itemDocRepository.save(check);

        updateCheckInfo(check);
        updateDocItems(check);

        return check;
    }

    private void setAdditionalFields(ItemDoc check) {
        check.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        check.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        check.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
    }
}
