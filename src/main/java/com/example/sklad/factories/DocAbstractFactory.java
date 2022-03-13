package com.example.sklad.factories;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.services.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DocAbstractFactory implements DocFactory{

    protected ItemDocDTO itemDocDTO;

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

    protected long getNewNumber(DocumentType docType) {
        return itemDocRepository.getLastNumber(docType.toString()) + 1;
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
}
