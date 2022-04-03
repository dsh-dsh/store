package com.example.store.factories.docs1s;

import com.example.store.factories.abstraction.DocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Doc1cFactory implements DocFactory {

    private DocDTO docDTO;
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
    public ItemDoc addDocument(DocDTO docDTO) {

        docType = DocumentType.getByValue(docDTO.getDocType());

        ItemDoc check = new ItemDoc();
        check.setNumber(docDTO.getNumber());
//        check.setDateTime(docDTO.getTime().toLocalDateTime());
        check.setProject(projectService.getByName(docDTO.getProject().getName()));
        check.setAuthor(userService.getByEmail(docDTO.getAuthor().getEmail()));
        check.setSupplier(companyService.getByName(docDTO.getSupplier().getName()));
        check.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
        check.setDocType(docType);

        switch (docType) {
            case CHECK_DOC:
                check.setIndividual(userService.getByEmail(docDTO.getIndividual().getEmail()));
                break;
            case WRITE_OFF_DOC:
                System.out.println("");
                break;
            case MOVEMENT_DOC:
                check.setStorageTo(storageService.getByName(docDTO.getStorageTo().getName()));
                break;
        }

        itemDocRepository.save(check);

        if(docType.equals(DocumentType.CHECK_DOC)) {
            checkInfoService.addCheckInfo(docDTO.getCheckInfo(), check);
        }

        addDocItems(docDTO, check);

        return check;
    }

    @Override
    public DocInterface updateDocument(DocDTO docDTO) {
        return null;
    }

    @Override
    public void deleteDocument(int docId) {}


    private void addDocItems(DocDTO docDTO, ItemDoc check) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, check));
    }

    public void setItemDocDTO(DocDTO docDTO) {
        this.docDTO = docDTO;
    }

    @Override
    public void holdDocument(Document document) {}

    @Override
    public void unHoldDocument(Document document){}
}
