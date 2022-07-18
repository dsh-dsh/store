package com.example.store.factories.docs1s;

import com.example.store.factories.abstraction.AbstractDocFactory;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Doc1cFactory  extends AbstractDocFactory {

//    @Autowired
//    private ProjectService projectService;
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private StorageService storageService;
//    @Autowired
//    private CompanyService companyService;
//    @Autowired
//    private ItemDocRepository itemDocRepository;
    @Autowired
    private DocItemServiceFor1CDocs docItemServiceFor1CDocs;
    @Autowired
    private CheckInfoServiceFor1CDock checkInfoServiceFor1CDock;
    @Autowired
    private LotService lotService;

    @Override
    public ItemDoc addDocument(DocDTO docDTO) {
        DocumentType docType = DocumentType.valueOf(docDTO.getDocType());
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setNumber(docDTO.getNumber());
        itemDoc.setDateTime(getNewTime(itemDoc, docDTO));
        itemDoc.setProject(projectService.getByName(docDTO.getProject().getName()));
        itemDoc.setAuthor(userService.getByEmail(docDTO.getAuthor().getEmail()));
        itemDoc.setSupplier(companyService.getByName(docDTO.getSupplier().getName()));
        itemDoc.setStorageFrom(storageService.getByName(docDTO.getStorageFrom().getName()));
        itemDoc.setDocType(docType);
        if(docType == DocumentType.CHECK_DOC) {
                itemDoc.setIndividual(userService.getByEmail(docDTO.getIndividual().getEmail()));
        }
        itemDocRepository.save(itemDoc);
        if(docType.equals(DocumentType.CHECK_DOC)) {
            checkInfoServiceFor1CDock.addCheckInfo(docDTO.getCheckInfo(), itemDoc);
        }
        addDocItems(docDTO, itemDoc);

        return itemDoc;
    }

    @Override
    public DocInterface updateDocument(DocDTO docDTO) {
        return null;
    } // doesn't need in this class

    @Override
    public void deleteDocument(int docId) {} // doesn't need in this class


    private void addDocItems(DocDTO docDTO, ItemDoc check) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemServiceFor1CDocs.addDocItem(docItemDTO, check));
    }

    @Override
    public void holdDocument(Document document) {
        lotService.addLotMovements(document);
        document.setHold(true);
        itemDocRepository.save((ItemDoc) document);
    }

    @Override
    public void unHoldDocument(Document document){} // doesn't need in this class
}
