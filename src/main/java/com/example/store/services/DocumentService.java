package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.ItemDocFactory;
import com.example.store.mappers.DocMapper;
import com.example.store.mappers.DocToListMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.responses.ListResponse;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private ItemDocFactory itemDocFactory;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private DocToListMapper docToListMapper;

    public boolean existsNotHoldenDocsBefore(Document document) {
        Storage storageFrom = null;
        Storage storageTo = null;
        if(document instanceof ItemDoc) {
            storageFrom = ((ItemDoc) document).getStorageFrom();
            storageTo = ((ItemDoc) document).getStorageTo();
        }
        return documentRepository.existsNotHoldenDocs(
                document.getDocType(), storageFrom, storageTo, document.getDateTime());
    }

    public void holdDocument(int docId) {
        ItemDoc document = (ItemDoc) getDocumentById(docId);
        if(existsNotHoldenDocsBefore(document)) {
            throw new BadRequestException(Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE);
        }
        itemDocFactory.holdDocument(document);
    }

    public void addDocument(DocDTO docDTO) {
        itemDocFactory.addDocument(docDTO);
    }

    public void updateDocument(DocDTO docDTO) {
        itemDocFactory.updateDocument(docDTO);
    }

    public List<Document> getDocumentsAfterAndInclude(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndDateTimeAfter(true, document.getDateTime(), Sort.by("dateTime").descending());
        documents.add(document);
        return documents;
    }

    public List<Document> getDocumentsByPeriod(Document currentDoc, Document limitDoc, boolean isHold) {
        return documentRepository.findByIsHoldAndDateTimeBetween(
                isHold, currentDoc.getDateTime(), limitDoc.getDateTime(), Sort.by("dateTime"));
    }

    public List<ItemDoc> getDocumentsByTypeAndStorageAndIsHold(DocumentType type, Storage storage, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return itemDocRepository.findByDocTypeAndStorageFromAndIsHoldAndDateTimeBetween(type, storage, isHold, from, to);
    }

    public List<OrderDoc> getDocumentsByTypeInAndProjectAndIsHold(List<DocumentType> types, Project project, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return orderDocRepository.findByDocTypeInAndProjectAndIsHoldAndDateTimeBetween(types, project, isHold, from, to);
    }

    public ListResponse<DocToListDTO> getDocumentsByType(DocumentType documentType, Pageable pageable) {
        Page<Document> page = documentRepository.findByDocType(documentType, pageable);
        List<DocToListDTO> dtoList = page.stream()
                .map(docToListMapper::mapToDocDTO)
                .collect(Collectors.toList());
        return new ListResponse<>(dtoList, page);
    }

    public List<ItemDoc> getItemDocsByType(DocumentType documentType) {
        return itemDocRepository.findByDocType(documentType);
    }

    public Document getDocumentById(int docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
    }

    public DocDTO getDocDTOById(int docId) {
        ItemDoc itemDoc = (ItemDoc) getDocumentById(docId);
        return docMapper.mapToDocDTO(itemDoc);
    }

    public void setHoldAndSave(boolean hold, Document document) {
        document.setHold(hold);
        documentRepository.save(document);
    }

    public void softDeleteDocument(DocDTO docDTO) {
        int docId = docDTO.getId();
        itemDocFactory.deleteDocument(docId);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    // TODO test all methods

    protected int getNextDocumentNumber(DocumentType type) {
        try {
            return documentRepository.getLastNumber(type.toString()) + 1;
        } catch (Exception exception) {
            return getStartDocNumber(type);
        }
    }

    private int getStartDocNumber(DocumentType documentType) {
        // todo add start number logic
        return 1;
    }
}
