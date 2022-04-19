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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Doc1cFactory implements DocFactory {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocItemServiceFor1CDocs docItemServiceFor1CDocs;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private CheckInfoServiceFor1CDock checkInfoServiceFor1CDock;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss");

    @Override
    public ItemDoc addDocument(DocDTO docDTO) {
        DocumentType docType = DocumentType.valueOf(docDTO.getDocType());
        ItemDoc check = new ItemDoc();
        check.setNumber(docDTO.getNumber());
        check.setDateTime(LocalDateTime.parse(docDTO.getTime(), timeFormatter));
        check.setProject(projectService.getByName(docDTO.getProject().getName()));
        check.setAuthor(userService.getByEmail(docDTO.getAuthor().getEmail()));
        check.setSupplier(companyService.getByName(docDTO.getSupplier().getName()));
        check.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
        check.setDocType(docType);
        if(docType == DocumentType.CHECK_DOC) {
                check.setIndividual(userService.getByEmail(docDTO.getIndividual().getEmail()));
        }
        itemDocRepository.save(check);
        if(docType.equals(DocumentType.CHECK_DOC)) {
            checkInfoServiceFor1CDock.addCheckInfo(docDTO.getCheckInfo(), check);
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
                .forEach(docItemDTO -> docItemServiceFor1CDocs.addDocItem(docItemDTO, check));
    }

    @Override
    public void holdDocument(Document document) {}

    @Override
    public void unHoldDocument(Document document){}
}
