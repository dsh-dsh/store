package com.example.sklad.factories.abstraction;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.entities.documents.DocInterface;
import com.example.sklad.model.entities.documents.Document;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.repositories.OrderDocRepository;
import com.example.sklad.services.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDocFactory implements DocFactory {

    protected ItemDocDTO docDTO;
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
    protected OrderDocRepository orderDocRepository;
    @Autowired
    protected CheckInfoService checkInfoService;

    @Override
    public DocInterface deleteDocument(ItemDocDTO itemDocDTO) {
        this.docDTO = itemDocDTO;
        ItemDoc document = getItemDoc();
        if(document.getDocType() == DocumentType.CHECK_DOC) {
            deleteCheckInfo(document);
        }
        deleteDocItems(document);
        deleteItemDoc(document);
        return document;
    }
    protected void deleteDocItems(ItemDoc itemDoc) {
        docItemService.deleteByDoc(itemDoc);
    }

    protected void deleteItemDoc(ItemDoc itemDoc) {
        itemDocRepository.deleteById(itemDoc.getId());
    }

    protected void deleteCheckInfo(ItemDoc check) {
        checkInfoService.deleteByDocId(check);
    }

    @NotNull
    protected ItemDoc getItemDoc() {
        int docId = docDTO.getId();
        return itemDocRepository.findById(docId)
                .orElseThrow(BadRequestException::new);
    }

    @NotNull
    protected OrderDoc getOrderDoc() {
        int docId = docDTO.getId();
        return orderDocRepository.getById(docId);
    }

    protected void setCommonFields(Document document) {
        document.setNumber(getNewNumber(documentType));
        document.setDateTime(docDTO.getTime().toLocalDateTime());
        document.setDocType(documentType);
        document.setProject(projectService.getById(docDTO.getProject().getId()));
        document.setAuthor(userService.getById(docDTO.getAuthor().getId()));
        document.setPayed(docDTO.isPayed());
        document.setHold(docDTO.isHold());
    }

    protected void updateCommonFields(Document document) {
        document.setNumber(docDTO.getNumber());
        document.setDateTime(docDTO.getTime().toLocalDateTime());
        document.setProject(projectService.getById(docDTO.getProject().getId()));
        document.setAuthor(userService.getById(docDTO.getAuthor().getId()));
        document.setPayed(docDTO.isPayed());
        document.setHold(docDTO.isHold());
    }

    protected int getNewNumber(DocumentType docType) {
        try {
           return itemDocRepository.getLastNumber(docType.toString()) + 1;
        } catch (Exception exception) {
            return getStartDocNumber(docType);
        }
    }

    private int getStartDocNumber(DocumentType documentType) {
        // todo add start number logic
        return 1;
    }

    protected void addCheckInfo(Document check) {
        checkInfoService.addCheckInfo(docDTO.getCheckInfo(), (ItemDoc) check);
    }

    protected void updateCheckInfo(Document check) {
        checkInfoService.updateCheckInfo(docDTO.getCheckInfo(), (ItemDoc) check);
    }

    protected void addDocItems(Document document) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, document));
    }

    protected void updateDocItems(Document document) {
        docItemService.updateDocItems(docDTO.getDocItems(), (ItemDoc) document);
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
