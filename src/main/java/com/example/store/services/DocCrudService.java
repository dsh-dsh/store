package com.example.store.services;

import com.example.store.components.EnvironmentVars;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.DocMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.ExceptionType;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocCrudService extends AbstractDocCrudService {

    @Autowired
    private DocMapper docMapper;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private HoldDocsService holdDocsService;
    @Autowired
    private DocsFrom1cService docsFrom1cService;
    @Autowired
    private EnvironmentVars env;

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

    public DocDTO getDocDTOById(int docId) {
        DocDTO dto;
        Document document = documentService.getDocumentById(docId);
        if(document instanceof ItemDoc) {
            dto = docMapper.mapToDocDTO((ItemDoc) document);
        } else {
            dto = docMapper.mapToDocDTO((OrderDoc) document);
        }
        return dto;
    }

    @Transaction
    public void addDocument(DocDTO docDTO) {
        checkTimePeriod(docDTO);
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            addOrderDoc(docDTO);
        } else {
            addItemDoc(docDTO);
        }
    }

    @Transaction
    public void updateDocument(DocDTO docDTO) {
        checkTimePeriod(docDTO);
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            updateOrderDocument(docDTO);
        } else {
            updateItemDoc(docDTO);
        }
    }

    @Transaction
    public void softDeleteDocument(DocDTO docDTO) {
        checkTimePeriod(docDTO);
        int docId = docDTO.getId();
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            deleteOrderDoc(docId);
        } else {
            deleteItemDoc(docId);
        }
    }

    public void holdDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        checkTimePeriod(document);
        if(holdDocsService.checkPossibilityToHold(document)) {
            holdDocsService.holdDocument(document);
        }
    }

    public void serialHoldDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        if(document.isHold()) {
            List<Document> documents = documentRepository
                    .findByIsHoldAndDateTimeAfter(true, document.getDateTime(),
                            Sort.by(Constants.DATE_TIME_STRING).descending());
            documents.forEach(doc -> holdDocsService.holdDocument(doc));
            holdDocsService.holdDocument(document);
        } else {
            List<Document> documents = documentRepository
                    .findByIsHoldAndIsDeletedAndDateTimeBefore(false, false,
                            document.getDateTime(), Sort.by(Constants.DATE_TIME_STRING));
            documents.add(document);
            documents.forEach(doc -> holdDocsService.holdDocument(doc));
        }
    }

    public DocDTO getDocDTOForControllerAdviceTest(int docId) {
        Document document = documentService.getDocumentById(docId);
        return docMapper.mapToDocDTO((ItemDoc) document);
    }

    public void addDocsFrom1C(ItemDocListRequestDTO itemDocListRequestDTO) {
        itemDocListRequestDTO.getCheckDTOList()
                .forEach(docsFrom1cService::addDocument);
    }

    @Transactional
    public Response<String> hardDeleteDocuments() {
        List<Document> documents = documentRepository
                .findByIsHoldAndIsDeletedAndDateTimeBefore(false, true, LocalDateTime.now(), Sort.by("id"));
        checkInfoService.deleteByDocs(documents);
        docItemService.deleteByDocs(documents);
        int count = documentRepository.deleteByIsDeleted(true);
        return new Response<>(Constants.OK, String.format(Constants.NUMBER_OF_DELETED_DOCS_MESSAGE, count));
    }

    protected void checkTimePeriod(DocDTO docDTO) {
        LocalDateTime docTime = Instant.ofEpochMilli(docDTO.getDateTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        if(docTime.isBefore(env.getPeriodStart())) {
            throw new BadRequestException(String.format(
                    Constants.OUT_OF_PERIOD_MESSAGE, env.getPeriodStart().toString()),
                    ExceptionType.COMMON_EXCEPTION);
        }
    }

    protected void checkTimePeriod(Document document) {
        if(document.getDateTime().isBefore(env.getPeriodStart())) {
            throw new BadRequestException(String.format(
                    Constants.OUT_OF_PERIOD_MESSAGE, env.getPeriodStart().toString()),
                    ExceptionType.COMMON_EXCEPTION);
        }
    }
}
