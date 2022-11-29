package com.example.store.services;

import com.example.store.components.PeriodStartDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.WarningException;
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
    private PeriodStartDateTime periodStartDateTime;
    @Autowired
    protected DocInfoService docInfoService;
    @Autowired
    private MailPeriodReportService mailPeriodReportService;

    public ListResponse<DocToListDTO> getDocumentsByFilter(String filter, long start, long end) {
        LocalDateTime startDate = Util.getLocalDateTime(start);
        LocalDateTime endDate = Util.getLocalDateTime(end).withHour(23);
        List<DocumentType> types;
        switch (filter) {
            case "default" :
                types = List.of(DocumentType.POSTING_DOC, DocumentType.RECEIPT_DOC, DocumentType.WRITE_OFF_DOC, DocumentType.MOVEMENT_DOC);
                break;
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

    public DocDTO getDocDTOById(int docId, boolean docCopy) {
        DocDTO dto;
        Document document = documentService.getDocumentById(docId);
        if(document instanceof ItemDoc) {
            dto = docMapper.mapToDocDTO((ItemDoc) document);
        } else {
            dto = docMapper.mapToDocDTO((OrderDoc) document);
        }
        dto.setDocInfo(docInfoService.getDocInfoDTOByDocument(document));
        if(docCopy) {
            dto.setNumber(getNewDocNumber(dto.getDocType()));
            dto.setId(0);
            dto.setHold(false);
            dto.setPayed(false);
            dto.setDateTime(Util.getLongLocalDateTime(LocalDateTime.now()));
        }
        return dto;
    }

    public DocDTO getMoveDocFromRequest(int docId) {
        Document document = documentService.getDocumentById(docId);
        DocDTO dto = docMapper.mapToDocDTO((ItemDoc) document);
        dto.setNumber(documentService.getNextDocumentNumber(DocumentType.MOVEMENT_DOC));
        dto.setDocType(DocumentType.MOVEMENT_DOC.getValue());
        dto.setId(0);
        dto.setHold(false);
        dto.setBaseDocumentId(document.getId());
        dto.getDocItems().forEach(item -> {
            item.setQuantityFact(item.getQuantity());
            item.setQuantity(0f);
            item.setAmount(0f);
        });
        return dto;
    }

    @Transaction
    public void addDocument(DocDTO docDTO, String saveTime) {
        checkTimePeriod(docDTO);
        this.saveTime = saveTime;
//        checkSaveTime(docDTO);
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
//        checkSaveTime(docDTO);
        if(docDTO.getDocType().equals(DocumentType.CREDIT_ORDER_DOC.getValue())
                || docDTO.getDocType().equals(DocumentType.WITHDRAW_ORDER_DOC.getValue())) {
            updateOrderDocument(docDTO);
        } else {
            updateItemDoc(docDTO);
        }
    }

    public void checkSaveTime(DocDTO docDTO) {
        if(saveTime.equals(Constants.DAY_START)) {
            LocalDateTime time = Util.getLocalDateTime(docDTO.getDateTime());
            if(documentService.existsHoldenDocumentsAfter(time)) {
                throw new WarningException(
                        Constants.HOLDEN_DOCS_EXISTS_AFTER_MESSAGE,
                        ExceptionType.UN_HOLD_EXCEPTION,
                        this.getClass().getName() + " - checkSaveTime(DocDTO docDTO)");
            }
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
        holdDocsService.checkDocItemQuantities(document);
        if(holdDocsService.checkPossibilityToHold(document)) {
            holdDocsService.holdDoc(document);
        }
    }

    public void unHoldDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        checkTimePeriod(document);
        if(holdDocsService.checkPossibilityToHold(document)) {
            holdDocsService.unHoldDoc(document);
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
        documents.add(document);
        for (Document doc : documents) {
            if(doc.getDocType() == DocumentType.CHECK_DOC) {
                throw new BadRequestException(
                        Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE,
                        this.getClass().getName() + " serialHold(Document document)");
            }
            holdDocsService.holdDoc(doc);
        }
    }

    protected void serialUnHold(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndDateTimeAfter(true, document.getDateTime(),
                        Sort.by(Constants.DATE_TIME_STRING).descending());
        documents = getAllChecksToUnHold(documents);
        documents.forEach(doc -> holdDocsService.unHoldDoc(doc));
        holdDocsService.unHoldDoc(document);
        softDeleteBaseDocs(documents);
    }

    protected void checkingFogUnHoldenChecks(List<Document> documents) {
        Optional<Document> optional = documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC).findFirst();
        if(optional.isPresent()) {
            throw new BadRequestException(
                    Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE,
                    this.getClass().getName() + " checkingFogUnHoldenChecks(List<Document> documents)");
        }
    }

    protected void softDeleteBaseDocs(List<Document> documents) {
        List<LocalDate> dates = getDatesOfChecks(documents);
        if(!dates.isEmpty()) {
            List<Document> writeOffDocs = documents.stream()
                    .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC)
                    .map(Document::getBaseDocument).distinct().collect(Collectors.toList());
            List<Document> otherDocs = documents.stream()
                    .filter(doc -> doc.getDocType() != DocumentType.CHECK_DOC)
                    .filter(doc -> isInList(writeOffDocs, doc))
                    .collect(Collectors.toList());
            documents.stream()
                    .filter(doc -> doc.getDocType() == DocumentType.CHECK_DOC)
                    .forEach(this::clearBaseDocInCheckDoc);
            otherDocs.forEach(doc -> {if(doc != null) softDeleteDoc(doc);});
            writeOffDocs.forEach(doc -> {if(doc != null) softDeleteDoc(doc);});
        }
    }

    protected void clearBaseDocInCheckDoc(Document document) {
        document.setBaseDocument(null);
        documentRepository.save(document);
    }

    private boolean isInList(List<Document> writeOffDocs, Document doc) {
        for (Document writeOffDoc : writeOffDocs) {
            if (doc.getBaseDocument() == writeOffDoc) return true;
        }
        return false;
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

    // add checks of the day that wasn't chosen
    protected List<Document> getAllChecksToUnHold(List<Document> documents) {
        // todo refactoring: simplify - get first doc and if it checkDoc find other checkDocs
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
        holdDocsService.unHoldDoc(document);
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
        itemDocListRequestDTO.getDocDTOList()
                .forEach(docsFrom1cService::addDocument);
        DocDTO docDTO = itemDocListRequestDTO.getDocDTOList().get(0);
        long reportDate = Util.getLongLocalDateTime(docDTO.getDate());
        mailPeriodReportService.sendPeriodReport(docDTO.getProject().getName(), reportDate);
    }

    @Transactional
    public void addInventoryDocFrom1C(ItemDocListRequestDTO itemDocListRequestDTO) {
        docsFrom1cService.setDocDateTime(null);
        itemDocListRequestDTO.getDocDTOList()
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
        if(docTime.isBefore(periodStartDateTime.get())) {
            throw new BadRequestException(
                    String.format(
                        Constants.OUT_OF_PERIOD_MESSAGE,
                            periodStartDateTime.get().toString()),
                    this.getClass().getName() + " checkTimePeriod(DocDTO docDTO)");
        }
    }

    protected void checkTimePeriod(Document document) {
        if(document.getDateTime().isBefore(periodStartDateTime.get())) {
            throw new BadRequestException(
                    String.format(
                        Constants.OUT_OF_PERIOD_MESSAGE,
                            periodStartDateTime.get().toString()),
                    this.getClass().getName() + " checkTimePeriod(Document document)");
        }
    }

    public int getNewDocNumber(String type) {
        DocumentType documentType = DocumentType.getByValue(type);
        return documentService.getNextDocumentNumber(documentType);
    }

    public String checkUnHoldenChecks() {
        Optional<Document> document = documentRepository
                .getFirstByDateTimeAfterAndDocTypeAndIsHoldAndIsDeleted(
                        periodStartDateTime.get(), DocumentType.CHECK_DOC,
                        false, false,
                        Sort.by(Constants.DATE_TIME_STRING));
        return document.map(value -> value.getDateTime().toString().substring(0, 10)).orElse("");
    }

    public String getLastCheckNumber(int prefix) {
        LocalDateTime periodStart = periodStartDateTime.get();
        long from = 1000000000L * prefix;
        long to = 1000000000L * (prefix + 1);
        Document doc = documentRepository.getLast1CDocNumber(from, to, periodStart).orElse(null);
        return doc != null? "<" + doc.getNumber() + ">"
                + doc.getDocType() + "*"
                + Util.getDateFourDigitsYear(doc.getDateTime()) : "";
    }

    @Transactional
    public void addPayment(int docId) {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(docId);
        OrderDoc orderDoc = getSupplierPaymentDoc(itemDoc);
        setPayed(itemDoc, orderDoc);
    }

    @Transactional
    public void deletePayment(int docId) {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(docId);
        softDeletePaymentDocOf(itemDoc);
        unSetPayed(itemDoc);
    }
}
