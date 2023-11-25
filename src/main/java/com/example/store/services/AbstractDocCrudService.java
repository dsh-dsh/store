package com.example.store.services;

import com.example.store.components.ReHoldChecking;
import com.example.store.components.UnHoldDocs;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.documents.DocInterface;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.repositories.ProjectRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDocCrudService {

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
    protected DocumentRepository documentRepository;
    @Autowired
    protected OrderDocRepository orderDocRepository;
    @Autowired
    protected CheckInfoService checkInfoService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ReHoldChecking reHoldChecking;
    @Autowired
    private LotService lotService;
    @Autowired
    private UnHoldDocs unHoldDocs;
    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;
    @Autowired
    protected SerialUnHoldDocService serialUnHoldDocService;

    // only for tests
    public List<Integer> getBlockingUserIds() {
        return blockingUserIds;
    }

    // only for tests
    public void setBlockingUserIds(List<Integer> blockingUserIds) {
        this.blockingUserIds = blockingUserIds;
    }

    protected DocumentType documentType;
    protected DocDTO docDTO;
    protected String saveTime;

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public DocInterface addItemDoc(DocDTO docDTO) {
        ItemDoc itemDoc = (ItemDoc) setDocument(getOrAddItemDoc(docDTO));
        setAdditionalFieldsAndSave(itemDoc);
        addDocumentItems(itemDoc, docDTO);
        if(docDTO.getDocType().equals(DocumentType.CHECK_DOC.getValue())) {
            addCheckInfo(itemDoc, docDTO);
        }
        docInfoService.setDocInfo(itemDoc, docDTO.getDocInfo());
        serialUnHoldDocService.unHold(itemDoc);
        return itemDoc;
    }

    public DocInterface addOrderDoc(DocDTO docDTO) {
        OrderDoc order = (OrderDoc) setDocument(getOrAddOrderDoc(docDTO));
        setAdditionalFieldsAndSave(order);
        docInfoService.setDocInfo(order, docDTO.getDocInfo());
        serialUnHoldDocService.unHold(order);
        return order;
    }

    public DocInterface updateItemDoc(DocDTO docDTO) {
        ItemDoc itemDoc = getOrAddItemDoc(docDTO);
        serialUnHoldDocService.unHold(itemDoc);
        ItemDoc updatedDoc = (ItemDoc) setDocument(itemDoc);
        setAdditionalFieldsAndSave(updatedDoc);
        updateDocItems(updatedDoc);
        updateCheckInfo(updatedDoc);
        docInfoService.setDocInfo(updatedDoc, docDTO.getDocInfo());
        return updatedDoc;
    }

    public DocInterface updateOrderDoc(DocDTO docDTO) {
        return addOrderDoc(docDTO);
    }

    public void deleteItemDoc(int docId) {
        ItemDoc itemDoc = itemDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_DOCUMENT_MESSAGE,
                        this.getClass().getName() + " - deleteItemDoc(int docId)"));
        serialUnHoldDocService.unHold(itemDoc);
        itemDoc.setDeleted(!itemDoc.isDeleted());
        itemDocRepository.save(itemDoc);
    }

    public void deleteOrderDoc(int docId) {
        OrderDoc order = orderDocRepository.findById(docId)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_DOCUMENT_MESSAGE,
                        this.getClass().getName() + " - deleteOrderDoc(int docId)"));
        serialUnHoldDocService.unHold(order);
        order.setDeleted(!order.isDeleted());
        orderDocRepository.save(order);
    }

    protected Document setDocument(Document document) {
        DocumentType docType = DocumentType.getByValue(docDTO.getDocType());
        setDocumentType(docType);
        setCommonFields(document, docDTO);
        return document;
    }

    protected ItemDoc getOrAddItemDoc(DocDTO docDTO) {
        setDocDTO(docDTO);
        int docId = docDTO.getId();
        if(docId != 0) {
            return itemDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_DOCUMENT_MESSAGE,
                            this.getClass().getName() + " - getOrAddItemDoc(DocDTO docDTO)"));
        } else {
            return new ItemDoc();
        }
    }

    protected OrderDoc getOrAddOrderDoc(DocDTO docDTO) {
        setDocDTO(docDTO);
        int docId = docDTO.getId();
        if(docId != 0) {
            return orderDocRepository.findById(docId)
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_DOCUMENT_MESSAGE,
                            this.getClass().getName() + " - getOrAddOrderDoc(DocDTO docDTO)"));
        } else {
            return new OrderDoc();
        }
    }

    protected void setDocDTO(DocDTO dto) {
        this.docDTO = dto;
    }

    protected void setAdditionalFieldsAndSave(Document document) {
        if (docDTO.getIndividual().getId() != 0) {
            document.setIndividual(userService.getById(docDTO.getIndividual().getId()));
        }
        if (docDTO.getSupplier().getId() != 0) {
            document.setSupplier(companyService.getById(docDTO.getSupplier().getId()));
        }
        if (docDTO.getRecipient().getId() != 0) {
            document.setRecipient(companyService.getById(docDTO.getRecipient().getId()));
        }
        if (document instanceof ItemDoc) {
            ItemDoc itemDoc = (ItemDoc) document;
                if (docDTO.getStorageTo().getId() != 0) {
                    itemDoc.setStorageTo(storageService.getById(docDTO.getStorageTo().getId()));
                }
            if (docDTO.getStorageFrom().getId() != 0) {
                itemDoc.setStorageFrom(storageService.getById(docDTO.getStorageFrom().getId()));
            }
            itemDocRepository.save(itemDoc);
        } else {
            OrderDoc order = (OrderDoc) document;
            order.setPaymentType(PaymentType.getByValue(docDTO.getPaymentType()));
            order.setAmount(docDTO.getAmount());
            order.setTax(docDTO.getTax());
            orderDocRepository.save(order);
        }
    }

    protected void addDocumentItems(ItemDoc document, DocDTO docDTO) {
        docDTO.getDocItems()
                .forEach(docItemDTO -> docItemService.addDocItem(docItemDTO, document));
    }

    protected void addCheckInfo(ItemDoc check, DocDTO docDTO) {
        checkInfoService.addCheckInfo(docDTO.getCheckInfo(), check);
    }

    protected void setCommonFields(Document document, DocDTO docDTO) {
        // todo update test due to line below
        long newDocNumber = document.getNumber() > 0 ?
                docDTO.getNumber() :
                documentService.getNextDocumentNumber(documentType);
        document.setNumber(newDocNumber);
        document.setDocType(documentType);
        document.setDateTime(getNewTime(document, docDTO));
        document.setProject(projectService.getById(docDTO.getProject().getId()));
        document.setAuthor(userService.getById(docDTO.getAuthor().getId()));
        document.setPayed(docDTO.isPayed());
        document.setHold(docDTO.isHold());
        setBaseDocument(document, docDTO);
    }

    protected void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    protected LocalDateTime getNewTime(Document document, DocDTO dto) {
        if(saveTime.equals(Constants.DTO_TIME)) {
            return Util.getLocalDateTime(dto.getDateTime());
        } else if(document.getDateTime() != null && saveTime.equals(Constants.CURRENT_TIME)) {
            return document.getDateTime();
        } else {
            LocalDate docDate = Util.getLocalDate(dto.getDateTime());
            return getNewDocTime(docDate,  saveTime.equals(Constants.DAY_START));
        }
    }

    protected LocalDateTime getNewDocTime(LocalDate docDate, boolean first) {
        LocalDateTime start = docDate.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        Optional<Document> optionalDocument = first ?
                documentRepository.getFirstNotCheckDoc(blockingUserIds, start, end) :
                documentRepository.getLastNotCheckDoc(blockingUserIds, start, end);
        if(optionalDocument.isPresent()) {
            return optionalDocument.get().getDateTime().plus(first ? -1 : 1, ChronoUnit.MILLIS);
        }
        return start.plusHours(Constants.START_HOUR_DOCS);
    }

    protected void setBaseDocument(Document document, DocDTO docDTO) {
        if(docDTO.getBaseDocumentId() > 0) {
            Document baseDoc = documentRepository.findById(docDTO.getBaseDocumentId())
                    .orElseThrow(() -> new BadRequestException(
                            Constants.NO_SUCH_BASE_DOCUMENT_MESSAGE,
                            this.getClass().getName() + " - setBaseDocument(Document document, DocDTO docDTO)"));
            document.setBaseDocument(baseDoc);
        }
    }

    protected void updateDocItems(ItemDoc document) {
        docItemService.updateDocItems(docDTO.getDocItems(), document);
    }

    protected void updateCheckInfo(ItemDoc check) {
        if(docDTO.getDocType().equals(DocumentType.CHECK_DOC.getValue())) {
            checkInfoService.updateCheckInfo(docDTO.getCheckInfo(), check);
        }
    }

    protected OrderDoc getSupplierPaymentDoc(Company supplier, List<Document> itemDocs) {
        float amount = 0;
        Project project = null;
        for(Document document : itemDocs) {
            amount += docItemService.getItemsAmount((ItemDoc) document);
            if(project == null) {
                project = document.getProject();
            }
        }
        return getOrderDoc(project, supplier, amount, null);
    }

    protected OrderDoc getSupplierPaymentDoc(ItemDoc itemDoc) {
        return getOrderDoc(itemDoc.getProject(), itemDoc.getSupplier(), docItemService.getItemsAmount(itemDoc), itemDoc);
    }

    protected OrderDoc getOrderDoc(Project project, Company supplier, float amount, ItemDoc itemDoc) {
        OrderDoc orderDoc = new OrderDoc();
        orderDoc.setNumber(documentService.getNextDocumentNumber(DocumentType.CREDIT_ORDER_DOC));
        orderDoc.setDocType(DocumentType.CREDIT_ORDER_DOC);
        orderDoc.setDateTime(getNewDocTime(LocalDate.now(), true));
        orderDoc.setPaymentType(PaymentType.SUPPLIER_PAYMENT);
        orderDoc.setProject(project);
        orderDoc.setAuthor(userService.getCurrentUser());
        orderDoc.setRecipient(supplier);
        orderDoc.setAmount(amount);
        orderDoc.setPayed(false);
        orderDoc.setBaseDocument(itemDoc);
        documentRepository.save(orderDoc);
        return orderDoc;
    }

    protected void setPayed(List<Document> itemDocs, OrderDoc orderDoc) {
        for(Document itemDoc : itemDocs) {
            itemDoc.setPayed(true);
            itemDoc.setBaseDocument(orderDoc);
            documentRepository.save(itemDoc);
        }
    }

    protected void setPayed(ItemDoc itemDoc, OrderDoc orderDoc) {
        itemDoc.setPayed(true);
        itemDoc.setBaseDocument(orderDoc);
        documentRepository.save(itemDoc);
    }

    protected void unSetPayed(ItemDoc itemDoc) {
        itemDoc.setPayed(false);
        itemDoc.setBaseDocument(null);
        documentRepository.save(itemDoc);
    }

    protected void softDeletePaymentDocOf(ItemDoc itemDoc) {
        Document paymentDoc = documentService.getBaseDocument(itemDoc);
        if(paymentDoc == null) return;
        if(paymentDoc.isHold()) {
            throw new BadRequestException(
                    Constants.ORDER_DOC_IS_HOLDEN_MESSAGE,
                    this.getClass().getName() + " - deletePayment(int docId)");
        }
        paymentDoc.setDeleted(true);
        paymentDoc.setBaseDocument(null);
        documentRepository.save(paymentDoc);
    }
}
