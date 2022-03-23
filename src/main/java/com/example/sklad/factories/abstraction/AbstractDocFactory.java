package com.example.sklad.factories.abstraction;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.dto.documents.DocDTO;
import com.example.sklad.model.entities.documents.Document;
import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.repositories.ItemDocRepository;
import com.example.sklad.repositories.OrderDocRepository;
import com.example.sklad.services.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class AbstractDocFactory implements DocFactory {

    public static final String NO_SUCH_DOCUMENT_MESSAGE = "no such document";
    protected DocDTO docDTO;
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

    @NotNull
    protected ItemDoc getItemDoc(DocDTO docDTO) {
        this.docDTO = docDTO;
        ItemDoc check = getOrAddItemDoc();
        DocumentType docType = DocumentType.getByValue(this.docDTO.getDocType());
        setDocumentType(docType);
        setCommonFields(check);
        return check;
    }

    @NotNull
    protected OrderDoc getOrderDoc(DocDTO docDTO) {
        this.docDTO = docDTO;
        OrderDoc order = getOrAddOrderDoc();
        DocumentType docType = DocumentType.getByValue(this.docDTO.getDocType());
        setDocumentType(docType);
        setCommonFields(order);
        return order;
    }

    @Override
    @Transactional
    public void deleteDocument(int docId) {
        ItemDoc document = getItemDoc(docId);
        if(document.getDocType() == DocumentType.CHECK_DOC) {
            checkInfoService.deleteByDocId(document);
        }
        docItemService.deleteByDoc(document);
        itemDocRepository.deleteById(document.getId());
    }

    @NotNull
    protected ItemDoc getItemDoc(int docId) {
            return itemDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(NO_SUCH_DOCUMENT_MESSAGE));
    }

    @NotNull
    protected OrderDoc getOrderDoc() {
        int docId = docDTO.getId();
        return orderDocRepository.getById(docId);
    }

    @NotNull
    protected OrderDoc getOrAddOrderDoc() {
        int docId = docDTO.getId();
        if(docId != 0) {
            return orderDocRepository.getById(docId);
        } else {
            return new OrderDoc();
        }
    }

    @NotNull
    protected ItemDoc getOrAddItemDoc() {
        int docId = docDTO.getId();
        if(docId != 0) {
            return itemDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(NO_SUCH_DOCUMENT_MESSAGE));
        } else {
            return new ItemDoc();
        }
    }

    protected void setCommonFields(Document document) {
        if(docDTO.getNumber() == 0) {
            document.setNumber(getNewNumber());
        } else {
            document.setNumber(docDTO.getNumber());
        }
        document.setDocType(documentType);
        document.setDateTime(getLocalDateTime(docDTO.getTime()));
        document.setProject(projectService.getById(docDTO.getProject().getId()));
        document.setAuthor(userService.getById(docDTO.getAuthor().getId()));
        document.setPayed(docDTO.isPayed());
        document.setHold(docDTO.isHold());
    }

    private LocalDateTime getLocalDateTime(long time) {
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    protected int getNewNumber() {
        try {
           return itemDocRepository.getLastNumber(documentType.toString()) + 1;
        } catch (Exception exception) {
            return getStartDocNumber(documentType);
        }
    }

    private int getStartDocNumber(DocumentType documentType) {
        // todo add start number logic
        return 1;
    }

    protected void addCheckInfo(ItemDoc check) {
        checkInfoService.addCheckInfo(docDTO.getCheckInfo(), check);
    }

    protected void updateCheckInfo(ItemDoc check) {
        checkInfoService.updateCheckInfo(docDTO.getCheckInfo(), check);
    }

    protected void addDocumentItems(ItemDoc document) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, document));
    }

    protected void updateDocItems(ItemDoc document) {
        docItemService.updateDocItems(docDTO.getDocItems(), document);
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }
}
