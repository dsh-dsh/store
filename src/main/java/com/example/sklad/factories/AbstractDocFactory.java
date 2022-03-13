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
        long docId = itemDocDTO.getId();
        ItemDoc check;
        if(docId != 0) {
            check = itemDocRepository.getById(docId);
        } else {
            check = new ItemDoc();
        }
        return check;
    }

    protected void setCommonFields(ItemDoc postingDoc) {
        postingDoc.setNumber(getNewNumber(documentType));
        postingDoc.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        postingDoc.setDocType(documentType);
        postingDoc.setProject(projectService.getById(itemDocDTO.getProject().getId()));
        postingDoc.setAuthor(userService.getById(itemDocDTO.getAuthor().getId()));
        postingDoc.setPayed(itemDocDTO.isPayed());
        postingDoc.setHold(itemDocDTO.isHold());
    }

    protected long getNewNumber(DocumentType docType) {
        try {
           return itemDocRepository.getLastNumber(docType.toString()) + 1;
        } catch (Exception exception) {
            return 1;
        }
    }

    protected void addCheckInfo(ItemDoc check) {
        checkInfoService.addCheckInfo(itemDocDTO.getCheckInfo(), check);
    }

    protected void addDocItems(ItemDoc check) {
        itemDocDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, check));
    }

    public void setItemDocDTO(ItemDocDTO itemDocDTO) {
        this.itemDocDTO = itemDocDTO;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
