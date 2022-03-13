package com.example.sklad.factories;

import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class CheckFactory extends DocAbstractFactory {

    private final DocumentType documentType = DocumentType.CHECK_DOC;

    @Override
    public DocInterface createDocument() {
        if (itemDocDTO == null) return null;
        ItemDoc check = getItemDoc();
        setFields(check);
        addCheckInfo(check);
        addDocItems(check);

        return check;
    }

    private void setFields(ItemDoc check) {
        check.setNumber(getNewNumber(documentType));
        check.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        check.setDocType(documentType);
        check.setProject(projectService.getById(itemDocDTO.getProject().getId()));
        check.setAuthor(userService.getById(itemDocDTO.getAuthor().getId()));
        check.setIndividual(userService.getById(itemDocDTO.getIndividual().getId()));
        check.setSupplier(companyService.getById(itemDocDTO.getSupplier().getId()));
        check.setStorageFrom(storageService.getById(itemDocDTO.getStorageFrom().getId()));
        check.setPayed(itemDocDTO.isPayed());
        check.setHold(itemDocDTO.isHold());
        itemDocRepository.save(check);
    }
}
