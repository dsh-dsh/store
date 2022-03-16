package com.example.sklad.factories.docs1s;

import com.example.sklad.factories.abstraction.DocFactory;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Doc1cFactory implements DocFactory {

    private ItemDocDTO itemDocDTO;
    private DocumentType docType;

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private CheckInfoService checkInfoService;

    @Override
    public ItemDoc addDocument(ItemDocDTO itemDocDTO) {

        docType = DocumentType.getByValue(itemDocDTO.getDocType());

        ItemDoc check = new ItemDoc();
        check.setNumber(itemDocDTO.getNumber());
        check.setDateTime(itemDocDTO.getTime().toLocalDateTime());
        check.setProject(projectService.getByName(itemDocDTO.getProject().getName()));
        check.setAuthor(userService.getByEmail(itemDocDTO.getAuthor().getEmail()));
        check.setSupplier(companyService.getByName(itemDocDTO.getSupplier().getName()));
        check.setStorageFrom(storageService.getByName(itemDocDTO.getStorageFrom().getName()));
        check.setDocType(docType);

        switch (docType) {
            case CHECK_DOC:
                check.setIndividual(userService.getByEmail(itemDocDTO.getIndividual().getEmail()));
                break;
            case WRITE_OFF_DOC:
                System.out.println("");
                break;
            case MOVEMENT_DOC:
                check.setStorageTo(storageService.getByName(itemDocDTO.getStorageTo().getName()));
                break;
        }

        itemDocRepository.save(check);

        if(docType.equals(DocumentType.CHECK_DOC)) {
            checkInfoService.addCheckInfo(itemDocDTO.getCheckInfo(), check);
        }

        addDocItems(itemDocDTO, check);

        return check;
    }

    @Override
    public DocInterface updateDocument(ItemDocDTO itemDocDTO) {
        return null;
    }

    private void addDocItems(ItemDocDTO itemDocDTO, ItemDoc check) {
        itemDocDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, check));
    }

    public void setItemDocDTO(ItemDocDTO itemDocDTO) {
        this.itemDocDTO = itemDocDTO;
    }
}
