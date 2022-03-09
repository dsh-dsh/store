package com.example.sklad.factories;

import com.example.sklad.model.dto.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.services.DocItemService;
import com.example.sklad.services.ProjectService;
import com.example.sklad.services.StorageService;
import com.example.sklad.services.UserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Setter
public class CheckFactory  implements DocFactory {

    ItemDocDTO itemDocDTO;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ItemDocRepository itemDocRepository;

    @Override
    public ItemDoc createDocument() {

        if (itemDocDTO == null) return null;

        ItemDoc check = new ItemDoc();
        check.setNumber(getNewNumber());
        check.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        check.setType(DocumentType.CHECK_DOC);
        check.setProject(projectService.getById(itemDocDTO.getProject().getId()));
        check.setAuthor(userService.getById(itemDocDTO.getAuthor().getId()));
        check.setIndividual(userService.getById(itemDocDTO.getIndividual().getId()));
        check.setStorageFrom(storageService.getById(itemDocDTO.getStorageFrom().getId()));
        check.setPayed(itemDocDTO.isPayed());
        check.setHold(itemDocDTO.isHold());
        ItemDoc newCheck = itemDocRepository.save(check);

        addDocItems(newCheck);

        return newCheck;
    }

    @Override
    public ItemDoc createDocumentFrom1C() {
        if (itemDocDTO == null) return null;

        ItemDoc check = new ItemDoc();
        check.setNumber(itemDocDTO.getNumber());
        check.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        check.setType(DocumentType.CHECK_DOC);
        check.setProject(projectService.getByName(itemDocDTO.getProject().getName()));
        check.setAuthor(userService.getByName(itemDocDTO.getAuthor().getName()));
        check.setIndividual(userService.getByName(itemDocDTO.getIndividual().getName()));
        check.setStorageFrom(storageService.getByName(itemDocDTO.getStorageFrom().getName()));
        ItemDoc newCheck = itemDocRepository.save(check);

        addDocItems(newCheck);

        return newCheck;
    }

    private long getNewNumber() {
        return itemDocRepository.getLastNumber(DocumentType.CHECK_DOC) + 1;
    }

    private void addDocItems(ItemDoc check) {
        itemDocDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, check));
    }

}
