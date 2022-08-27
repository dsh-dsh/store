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
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void addDocument(DocDTO docDTO, String saveTime) {
        checkTimePeriod(docDTO);
        this.saveTime = saveTime;
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            addOrderDoc(docDTO);
        } else {
            addItemDoc(docDTO);
        }
    }

    @Transaction
    public void updateDocument(DocDTO docDTO, String saveTime) {
        checkTimePeriod(docDTO);
        this.saveTime = saveTime;
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

    @Transactional
    public void serialHoldDocuments(int docId) {
        Document document = documentService.getDocumentById(docId);
        checkTimePeriod(document);
        if(document.isHold()) {
            serialUnHold(document);
        } else {
            serialHold(document);
        }
    }

    protected void serialHold(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndIsDeletedAndDateTimeBefore(false, false,
                        document.getDateTime(), Sort.by(Constants.DATE_TIME_STRING));
        checkingFogUnHoldenChecks(documents);
        documents.add(document);
        documents.forEach(doc -> holdDocsService.holdDocument(doc));
    }

    protected void serialUnHold(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndDateTimeAfter(true, document.getDateTime(),
                        Sort.by(Constants.DATE_TIME_STRING).descending());
        documents = getAllChecksToUnHold(documents);
        List<LocalDate> dates = getDatesOfChecks(documents);
        softDeleteBaseDocs(documents, dates);
        documents.forEach(doc -> holdDocsService.holdDocument(doc));
        holdDocsService.holdDocument(document);
    }

    protected void checkingFogUnHoldenChecks(List<Document> documents) {
        Optional<Document> optional = documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC).findFirst();
        if(optional.isPresent()) {
            throw new BadRequestException(Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE);
        }
    }

    protected void softDeleteBaseDocs(List<Document> documents, List<LocalDate> dates) {
        if(!dates.isEmpty()) {
            List<Document> writeOffDocs = documents.stream()
                    .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC)
                    .map(Document::getBaseDocument).distinct().collect(Collectors.toList());
            List<Document> postingDocs = writeOffDocs.stream().map(Document::getBaseDocument).collect(Collectors.toList());
            postingDocs.forEach(this::softDeleteDoc);
            writeOffDocs.forEach(this::softDeleteDoc);
        }
    }

    protected List<LocalDate> getDatesOfChecks(List<Document> documents) {
        List<LocalDate> dates = new ArrayList<>();
        if(!documents.isEmpty()) {
            dates = documents.stream()
                    .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC)
                    .map(check -> check.getDateTime().toLocalDate())
                    .distinct().collect(Collectors.toList());
        }
        return dates;
    }

    protected List<Document> getAllChecksToUnHold(List<Document> documents) {
        List<Document> checks = documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC).collect(Collectors.toList());
        if(!checks.isEmpty()) {
            LocalDateTime to = checks.get(checks.size()-1).getDateTime().minus(1, ChronoUnit.MILLIS);
            LocalDateTime from = to.toLocalDate().atStartOfDay();
            List<Document> checksBefore
                    = documentRepository.findByDocTypeAndIsHoldAndIsDeletedAndDateTimeBetween(
                    DocumentType.CHECK_DOC, true, false,
                    from, to, Sort.by(Constants.DATE_TIME_STRING).descending());
            if(!checksBefore.isEmpty()) {
                documents = Stream.of(checksBefore, documents)
                        .flatMap(Collection::stream).collect(Collectors.toList());
            }
        }
        return documents;
    }

    protected void softDeleteDoc(Document document) {
        holdDocsService.holdDocument(document);
        document.setDeleted(true);
        documentRepository.save(document);
    }

    public DocDTO getDocDTOForControllerAdviceTest(int docId) {
        Document document = documentService.getDocumentById(docId);
        return docMapper.mapToDocDTO((ItemDoc) document);
    }

    @Transactional
    public void addDocsFrom1C(ItemDocListRequestDTO itemDocListRequestDTO) {
        docsFrom1cService.setDocDateTime(null);
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
        LocalDateTime docTime = Util.getLocalDateTime(docDTO.getDateTime());
        if(docTime.isBefore(env.getPeriodStart())) {
            throw new BadRequestException(String.format(
                    Constants.OUT_OF_PERIOD_MESSAGE, env.getPeriodStart().toString()));
        }
    }

    protected void checkTimePeriod(Document document) {
        if(document.getDateTime().isBefore(env.getPeriodStart())) {
            throw new BadRequestException(String.format(
                    Constants.OUT_OF_PERIOD_MESSAGE, env.getPeriodStart().toString()));
        }
    }

    public int getNewDocNumber(String type) {
        DocumentType documentType = DocumentType.getByValue(type);
        return getNextDocumentNumber(documentType);
    }

    public String checkUnHoldenChecks() {
        Optional<Document> document = documentRepository
                .getFirstByDateTimeAfterAndDocTypeAndIsHoldAndIsDeleted(
                        env.getPeriodStart(), DocumentType.CHECK_DOC,
                        false, false,
                        Sort.by(Constants.DATE_TIME_STRING));
        return document.map(value -> value.getDateTime().toString().substring(0, 10)).orElse("");
    }
}
