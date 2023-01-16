package com.example.store.services;

import com.example.store.components.PeriodDateTime;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.WarningException;
import com.example.store.mappers.DocMapper;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.documents.DocToPaymentDTO;
import com.example.store.model.dto.requests.FixShortagesRequest;
import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.DocInfo;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.ExceptionType;
import com.example.store.model.responses.Response;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.example.store.utils.annotations.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private PeriodDateTime periodDateTime;
    @Autowired
    protected DocInfoService docInfoService;
    @Autowired
    private MailPeriodReportService mailPeriodReportService;
    @Autowired
    private SerialUnHoldDocService serialUnHoldDocService;

    public List<DocToListDTO> getDocumentsByFilter(String filter, long start, long end) {
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
        return list.stream()
                .map(doc -> {
                    if(doc instanceof ItemDoc) {
                        return docMapper.mapToDocToListDTO((ItemDoc) doc);
                    } else {
                        return docMapper.mapToDocToListDTO((OrderDoc) doc);
                    }
                })
                .collect(Collectors.toList());
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
        checkTimePeriod(Util.getLocalDateTime(docDTO.getDateTime()));
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
        checkTimePeriod(Util.getLocalDateTime(docDTO.getDateTime()));
        this.saveTime = saveTime;
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
        checkTimePeriod(Util.getLocalDateTime(docDTO.getDateTime()));
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
        checkTimePeriod(document.getDateTime());
        holdDocsService.checkDocItemQuantities(document);
        if(holdDocsService.checkPossibilityToHold(document)) {
            holdDocsService.holdDoc(document);
        }
        holdRelativeDocs(document);
    }

    // todo add tests
    protected void holdRelativeDocs(Document document) {
        if(document.getDocType() == DocumentType.INVENTORY_DOC) {
            List<Integer> relativeDocIdList = getRelativeDocIds(document.getId());
            relativeDocIdList.forEach(this::holdDocument);
        }
    }

    public void unHoldDocument(int docId) {
        Document document = documentService.getDocumentById(docId);
        unHoldRelativeDocs(document);
        checkTimePeriod(document.getDateTime());
        if(holdDocsService.checkPossibilityToHold(document)) {
            holdDocsService.unHoldDoc(document);
        }
    }

    // todo add tests
    protected void unHoldRelativeDocs(Document document) {
        if(document.getDocType() == DocumentType.INVENTORY_DOC) {
            List<Integer> relativeDocIdList = getRelativeDocIds(document.getId());
            relativeDocIdList.stream()
                    .sorted(Comparator.reverseOrder())
                    .map(documentService::getDocumentById)
                    .forEach(this::softDeleteDoc);
        }
    }

    @Transactional
    public void serialHoldDocuments(int docId) {
        Document document = documentService.getDocumentById(docId);
        checkTimePeriod(document.getDateTime());
        if(document.isHold()) {
            serialUnHoldDocService.unHold(document);
        } else {
            serialHold(document);
        }
    }

    protected void serialHold(Document document) {
        List<Document> documents = documentRepository
                .findByIsHoldAndIsDeletedAndDateTimeBefore(false, false,
                        document.getDateTime(), Sort.by(Constants.DATE_TIME_STRING));
        documents.add(document);
        if(documents.stream().anyMatch(doc -> doc.getDocType() == DocumentType.CHECK_DOC)) {
            throw new BadRequestException(
                    Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE,
                    this.getClass().getName() + " serialHold(Document document)");
        }
        documents.forEach(holdDocsService::holdDoc);
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

    protected void checkTimePeriod(LocalDateTime docTime) {
        if(docTime.isBefore(periodDateTime.getStartDateTime())
                || docTime.isAfter(periodDateTime.getEndDateTime())) {
            throw new BadRequestException(
                    String.format(
                        Constants.OUT_OF_PERIOD_MESSAGE,
                            periodDateTime.getStartDateTime().toString(),
                            periodDateTime.getEndDateTime().toString()),
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
                        periodDateTime.getStartDateTime(), DocumentType.CHECK_DOC,
                        false, false,
                        Sort.by(Constants.DATE_TIME_STRING));
        return document.map(value -> value.getDateTime().toString().substring(0, 10)).orElse("");
    }

    public String getLastCheckNumber(int prefix) {
        LocalDateTime periodStart = periodDateTime.getStartDateTime().minusDays(30);
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

    @Transactional
    public void addSupplierPayments(String supplierName) {
        Company supplier = companyService.getByName(supplierName);
        List<Document> documents = documentService.getDocsToPay(supplier);
        OrderDoc orderDoc = getSupplierPaymentDoc(supplier, documents);
        setPayed(documents, orderDoc);
    }

    public List<DocToPaymentDTO> getDocsDTOToPay(int companyId) {
        Company supplier = companyService.findById(companyId);
        return documentService.getDocsToPay(supplier).stream()
                .map(doc -> {
                    DocToPaymentDTO dto = docMapper.mapToDocToPaymentDTO((ItemDoc) doc);
                    DocInfo docInfo = docInfoService.getDocInfoByDocument(doc);
                    if(docInfo != null) {
                        dto.setSupplierDocNumber(docInfo.getSupplierDocNumber());
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void addRelativeDocuments(ItemDocListRequestDTO itemDocListRequestDTO, int docId) {
        Document parentDoc = documentService.getDocumentById(docId);
        List<DocDTO> docDTOList = itemDocListRequestDTO.getDocDTOList();
        documentService.shiftTimeInDocsAfter(parentDoc, docDTOList.size());
        for(int i = 0; i < docDTOList.size(); i++) {
            DocDTO dto = docDTOList.get(i);
            int offset = i + 1;
            dto.setDateTime(Util.getLongLocalDateTime(parentDoc.getDateTime()) + offset);
            addDocument(dto, Constants.DTO_TIME);
        }
    }

    // todo add tests
    public List<Integer> getRelativeDocIds(int docId) {
        return documentRepository.getRelativeDocIds(docId);
    }

    // todo add tests
    public void fixShortagesAndHold(FixShortagesRequest request) {
        ItemDoc itemDoc = (ItemDoc) documentService.getDocumentById(request.getDocId());
        docItemService.fixShortages(itemDoc, request.getShortages());
        holdDocument(itemDoc.getId());
    }
}
