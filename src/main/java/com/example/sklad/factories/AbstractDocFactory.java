package com.example.sklad.factories;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.services.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDocFactory implements DocFactory{

    protected ItemDocDTO itemDocDTO;
    protected DocumentType documentType;

    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected DocItemService docItemService;
    @Autowired
    protected StorageService storageService;
    @Autowired
    protected CompanyService companyService;
    @Autowired
    protected ItemDocRepository itemDocRepository;
    @Autowired
    protected CheckInfoService checkInfoService;

    @NotNull
    protected ItemDoc getItemDoc() {
        int docId = itemDocDTO.getId();
        return itemDocRepository.getById(docId);
    }

    protected void setCommonFields(ItemDoc itemDoc) {
        itemDoc.setNumber(getNewNumber(documentType));
        itemDoc.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        itemDoc.setDocType(documentType);
        itemDoc.setProject(projectService.getById(itemDocDTO.getProject().getId()));
        itemDoc.setAuthor(userService.getById(itemDocDTO.getAuthor().getId()));
        itemDoc.setPayed(itemDocDTO.isPayed());
        itemDoc.setHold(itemDocDTO.isHold());
    }

    protected void updateCommonFields(ItemDoc itemDoc) {
        itemDoc.setNumber(itemDocDTO.getNumber());
        itemDoc.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        itemDoc.setProject(projectService.getById(itemDocDTO.getProject().getId()));
        itemDoc.setAuthor(userService.getById(itemDocDTO.getAuthor().getId()));
        itemDoc.setPayed(itemDocDTO.isPayed());
        itemDoc.setHold(itemDocDTO.isHold());
    }

    protected int getNewNumber(DocumentType docType) {
        try {
           return itemDocRepository.getLastNumber(docType.toString()) + 1;
        } catch (Exception exception) {
            return 1;
        }
    }

    protected void addCheckInfo(ItemDoc check) {
        checkInfoService.addCheckInfo(itemDocDTO.getCheckInfo(), check);
    }

    protected void updateCheckInfo(ItemDoc check) {
        checkInfoService.updateCheckInfo(itemDocDTO.getCheckInfo(), check);
    }

    protected void addDocItems(ItemDoc check) {
        itemDocDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, check));
    }

    protected void updateDocItems(ItemDoc check) {
        docItemService.updateDocItems(itemDocDTO.getDocItems(), check);
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
