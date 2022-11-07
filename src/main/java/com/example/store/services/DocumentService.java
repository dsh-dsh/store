package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.DocMapper;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocMapper docMapper;
    @Autowired
    private CheckInfoService checkInfoService;

    public Document getFirstUnHoldenCheck(LocalDateTime from) {
        return documentRepository.getFirstByDateTimeAfterAndDocTypeAndIsHoldAndIsDeleted(from,
                DocumentType.CHECK_DOC, false, false, Sort.by(Constants.DATE_TIME_STRING))
                .orElseThrow(() -> new BadRequestException(
                        Constants.NOT_HOLDEN_CHECKS_DOCS_NOT_EXIST_MESSAGE,
                        this.getClass().getName() + " - getFirstUnHoldenCheck(LocalDateTime from)"));
    }

    public List<Document> getDocumentsAfterAndInclude(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndDateTimeAfter(true, document.getDateTime(), Sort.by(Constants.DATE_TIME_STRING).descending());
        documents.add(document);
        return documents;
    }

    public List<ItemDoc> getDocumentsByTypeAndStorageAndIsHold(DocumentType type, Storage storage, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return itemDocRepository.findByDocTypeAndStorageFromAndIsHoldAndDateTimeBetween(type, storage, isHold, from, to);
    }

    public List<OrderDoc> getDocumentsByTypeInAndProjectAndIsHold(List<DocumentType> types, Project project, boolean isHold, LocalDateTime from, LocalDateTime to) {
        return orderDocRepository.findByDocTypeInAndProjectAndIsHoldAndDateTimeBetween(types, project, isHold, from, to);
    }

    public List<ItemDoc> getItemDocsByType(DocumentType documentType) {
        return itemDocRepository.findByDocType(documentType);
    }

    public Document getDocumentById(int docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_DOCUMENT_MESSAGE,
                        this.getClass().getName() + " - getDocumentById(int docId)"));
    }

    public void setIsHoldAndSave(boolean hold, Document document) {
        document.setHold(hold);
        documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    protected int getNextDocumentNumber(DocumentType type) {
        try {
            return documentRepository.getLastNumber(type.toString()) + 1;
        } catch (Exception exception) {
            return Constants.START_DOCUMENT_NUMBER;
        }
    }
}
