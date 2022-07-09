package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.factories.ItemDocFactory;
import com.example.store.factories.OrderDocFactory;
import com.example.store.mappers.DocMapper;
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
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
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
    private OrderDocFactory orderDocFactory;
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

    public boolean existsNotHoldenDocsBefore(Document document) {
        if(document.isHold()) {
            return false;
        }
        return documentRepository.existsByDateTimeLessThanAndIsDeletedAndIsHold(document.getDateTime(), false,false);
    }

    public void holdDocument(int docId) {
        Document document = getDocumentById(docId);
        if(existsNotHoldenDocsBefore(document)) {
            throw new BadRequestException(Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE);
        }
        if(document instanceof ItemDoc) {
            if(document.isHold()) {
                itemDocFactory.unHoldDocument(document);
            } else {
                itemDocFactory.holdDocument(document);
            }
        } else {
            if(document.isHold()) {
                orderDocFactory.unHoldDocument(document);
            } else {
                orderDocFactory.holdDocument(document);
            }
        }
    }

    public void addDocument(DocDTO docDTO) {
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            orderDocFactory.addDocument(docDTO);
        } else {
            itemDocFactory.addDocument(docDTO);
        }
    }

    public void updateDocument(DocDTO docDTO) {
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
            || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            orderDocFactory.updateDocument(docDTO);
        } else {
            itemDocFactory.updateDocument(docDTO);
        }
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

    public ListResponse<DocToListDTO> getDocumentsByFilter(String filter, long start, long end) {
        LocalDateTime startDate = Util.getLocalDateTime(start);
        LocalDateTime endDate = Util.getLocalDateTime(end);
        List<DocumentType> types;
        switch (filter) {
            case "posting" :
               types = List.of(DocumentType.POSTING_DOC);
               break;
            case "store" :
                types = List.of(DocumentType.RECEIPT_DOC, DocumentType.WRITE_OFF_DOC, DocumentType.MOVEMENT_DOC);
                break;
            case "request" :
                types = List.of(DocumentType.REQUEST_DOC);
                break;
            case "order" :
                types = List.of(DocumentType.WITHDRAW_ORDER_DOC, DocumentType.CREDIT_ORDER_DOC);
                break;
            case "check" :
                types = List.of(DocumentType.CHECK_DOC);
                break;
            case "invent" :
                types = List.of(DocumentType.INVENTORY_DOC);
                break;
            default:
                types = null;
        }
        List<Document> list = documentRepository.findByDocInFilter(filter, types, startDate, endDate);
        List<DocToListDTO> dtoList = list.stream()
                .map(doc -> {
                    if(doc instanceof ItemDoc) {
                        return docMapper.mapToDocToListDTO((ItemDoc) doc);
                    } else {
                        return docMapper.mapToDocToListDTO((OrderDoc) doc);
                    }
                })
                .collect(Collectors.toList());
        return new ListResponse<>(dtoList);
    }

    public List<ItemDoc> getItemDocsByType(DocumentType documentType) {
        return itemDocRepository.findByDocType(documentType);
    }

    public Document getDocumentById(int docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_DOCUMENT_MESSAGE));
    }

    public DocDTO getDocDTOById(int docId) {
        DocDTO dto;
        Document document = getDocumentById(docId);
        if(document instanceof ItemDoc) {
            dto = docMapper.mapToDocDTO((ItemDoc) document);
        } else {
            dto = docMapper.mapToDocDTO((OrderDoc) document);
        }
        return dto;
    }

    public void setHoldAndSave(boolean hold, Document document) {
        document.setHold(hold);
        documentRepository.save(document);
    }

    public void softDeleteDocument(DocDTO docDTO) {
        int docId = docDTO.getId();
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            orderDocFactory.deleteDocument(docId);
        } else {
            itemDocFactory.deleteDocument(docId);
        }
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
