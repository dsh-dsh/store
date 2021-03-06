package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.ExceptionType;
import com.example.store.mappers.DocMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            addOrderDoc(docDTO);
        } else {
            addItemDoc(docDTO);
        }
    }

    @Transaction
    public void updateDocument(DocDTO docDTO) {
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            updateOrderDocument(docDTO);
        } else {
            updateItemDoc(docDTO);
        }
    }

    @Transaction
    public void softDeleteDocument(DocDTO docDTO) {
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
        if(holdDocsService.existsNotHoldenDocsBefore(document)) {
            throw new BadRequestException(
                    Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE,
                    ExceptionType.HOLD_EXCEPTION);
        }
        holdDocsService.holdDocument(document);
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
}
