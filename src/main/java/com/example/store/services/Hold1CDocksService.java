package com.example.store.services;

import com.example.store.components.SystemSettingsCash;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.exceptions.UnHoldenDocsException;
import com.example.store.exceptions.WarningException;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.*;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class Hold1CDocksService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private HoldDocsService holdDocsService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private CheckInfoService checkInfoService;
    @Autowired
    private OrderDocRepository orderDocRepository;
    @Autowired
    private SettingService settingService;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private User systemUser;
    @Autowired
    private MailService mailService;
    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private SystemSettingsCash systemSettingsCash;
    @Autowired
    @Qualifier("disabledItemIds")
    private List<Integer> disabledItemIds;

    public static final boolean BY_CARD_KEY = true;
    public static final boolean BY_CASH_KEY = false;

    public static final int CHECK_DOC_START_HOUR = 2;

    @Value("${spring.mail.to.email}")
    private String toEmail;

    private ItemDoc receiptDoc;
    private ItemDoc writeOffDoc;
    private List<ItemDoc> checks;
    private LocalDateTime last1CDocTime;
    private LocalDateTime nextDocTime;
    private Boolean ignoreMissingDocs;

    @Transactional
    public void holdFirstUnHoldenChecks(Boolean ignoreMissingDocs) {
        LocalDateTime from = getFirstUnHoldenCheckDate();
        LocalDateTime to = from.plusDays(1);
        this.ignoreMissingDocs = ignoreMissingDocs;
        hold1CDocsByPeriod(from, to);
    }

    @Transactional
    public void hold1CDocsByPeriod(LocalDateTime from, LocalDateTime to) {
        setNextDocTime(null);
        checkExistingNotHoldenDocsBefore(from);
        List<Project> projects = projectService.getProjectListToHold();
        checkExistingAllProjectsDocs(projects, from, to);
        List<Storage> storages = storageService.getStorageList(from.toLocalDate());
        setNextDocTime(from);
        for (Storage storage : storages) {
            checks = getUnHoldenChecksByStorageAndPeriod(storage, from, to);
            if (checks.isEmpty()) continue;
            createDocsToHoldByStoragesAndPeriod(storage, to);
            createCreditOrders(storage);
            holdDocsAndChecksByStoragesAndPeriod();
            receiptDoc = null;
            writeOffDoc = null;
        }
        projects.forEach(project -> holdOrdersByProjectsAndPeriod(project, from, to));
        checkUnHoldenDocksExists(to, false);
    }

    // todo tests
    protected void setNextDocTime(LocalDateTime dateTime) {
        if(dateTime == null) {
            nextDocTime = null;
            return;
        }
        if(nextDocTime == null) {
            nextDocTime = dateTime.toLocalDate().atStartOfDay().withHour(3);
        } else {
            nextDocTime = nextDocTime.plus(1, ChronoUnit.MILLIS);
        }
    }

    // todo tests
    protected LocalDateTime getNextDocTime() {
        if(nextDocTime == null) {
            throw new HoldDocumentException("Ошибка даты документа");
        } else {
            LocalDateTime current = nextDocTime;
            nextDocTime = nextDocTime.plus(1, ChronoUnit.MILLIS);
            return current;
        }
    }

    protected LocalDateTime getFirstUnHoldenCheckDate() {
        Period period = periodService.getCurrentPeriod();
        return documentService.getFirstUnHoldenCheck(period.getStartDate().atStartOfDay())
                .getDateTime().toLocalDate().atStartOfDay();
    }


    protected void createCreditOrders(Storage storage) {
        Project project = projectService.getProjectByStorageName(storage.getName()).orElse(null);
        if(project == null) return;
        for(Map.Entry<PaymentType, Float> entry : getPaymentAmountMap(getSumMap()).entrySet()) {
            createOrderDoc(entry.getValue(), entry.getKey(), project, getNextDocTime());
        }
    }

    protected Map<PaymentType, Float> getPaymentAmountMap(Map<CheckPaymentType, Float> sumMap) {
        Map<CheckPaymentType, PaymentType> enumMap = new EnumMap<>(CheckPaymentType.class);
        enumMap.put(CheckPaymentType.CARD_PAYMENT, PaymentType.SALE_CARD_PAYMENT);
        enumMap.put(CheckPaymentType.CASH_PAYMENT, PaymentType.SALE_CASH_PAYMENT);
        enumMap.put(CheckPaymentType.QR_PAYMENT, PaymentType.SALE_QR_PAYMENT);
        enumMap.put(CheckPaymentType.DELIVERY_PAYMENT, PaymentType.SALE_DELIVERY_PAYMENT);
        return sumMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> enumMap.get(entry.getKey()), Map.Entry::getValue));
    }

    protected Map<CheckPaymentType, Float> getSumMap() {
        return checks.stream()
                .collect(Collectors.toMap(
                        checkInfoService::getCheckPaymentType,
                        docItemService::getItemsAmount,
                        Float::sum));
    }

    protected void createOrderDoc(float sum, PaymentType type, Project project, LocalDateTime time) {
        DocumentType docType = DocumentType.WITHDRAW_ORDER_DOC;
        OrderDoc order = new OrderDoc();
        order.setNumber(documentService.getNextDocumentNumber(docType));
        order.setDateTime(time);
        order.setDocType(docType);
        order.setProject(project);
        order.setAuthor(systemUser);
        order.setRecipient(companyService.getOurCompany());
        order.setPayed(false);
        order.setBaseDocument(writeOffDoc);
        order.setPaymentType(type);
        order.setAmount(sum);
        orderDocRepository.save(order);
    }

    protected void holdDocsBefore() {
        Period currentPeriod = periodService.getCurrentPeriod();
        List<Document> documents = documentRepository.findByIsHoldAndIsDeletedAndDateTimeBetween(
                        false, false,
                        currentPeriod.getStartDate().atStartOfDay(),
                        currentPeriod.getEndDate().atStartOfDay(),
                        Sort.by(Constants.DATE_TIME_STRING));
        documents.stream()
                .filter(doc -> (doc.getDocType() != DocumentType.CHECK_DOC
                        && doc.getDocType() != DocumentType.CREDIT_ORDER_DOC
                        && doc.getDocType() != DocumentType.WITHDRAW_ORDER_DOC))
                .forEach(doc -> holdDocsService.holdDoc(doc));
    }

    protected void createDocsToHoldByStoragesAndPeriod(Storage storage, LocalDateTime to) {
        Project project = checks.get(0).getProject();
        Map<Item, BigDecimal> itemMap = getItemMapFromCheckDocs(checks);
        Map<Item, BigDecimal> writeOffItemMap = ingredientService.getIngredientQuantityMap(itemMap, to.toLocalDate());
        if(systemSettingsCash.getProperty(SettingType.ADD_REST_FOR_HOLD_1C_DOCS) == 1) {
            Map<Item, BigDecimal> receiptItemMap = getReceiptItemMap(writeOffItemMap, storage, to);
            receiptDoc = createReceiptDoc(storage, project, receiptItemMap, getNextDocTime());
        }
        writeOffDoc = createWriteOffDocForChecks(storage, project, writeOffItemMap, getNextDocTime());
    }

    protected void holdDocsAndChecksByStoragesAndPeriod() {
        if (receiptDoc != null) {
            receiptDoc.setBaseDocument(writeOffDoc);
            holdDocsService.holdDoc(receiptDoc);
        }
        if(writeOffDoc != null) {
            holdDocsService.hold1CDoc(writeOffDoc);
            checks.forEach(check -> {
                check.setBaseDocument(writeOffDoc);
                documentService.setIsHoldAndSave(true, check);
            });
        }
    }

    protected void holdOrdersByProjectsAndPeriod(Project project, LocalDateTime from, LocalDateTime to) {
        List<OrderDoc> orders = getUnHoldenOrdersByProjectAndPeriod(project, from, to);
        for(OrderDoc order : orders) {
            holdDocsService.holdDoc(order);
        }
    }

    protected Map<Item, BigDecimal> getReceiptItemMap(Map<Item, BigDecimal> writeOffItemMap, Storage storage, LocalDateTime time) {
        List<Item> writeOfItems = new ArrayList<>(writeOffItemMap.keySet());
        Map<Item, BigDecimal> itemRestMap = itemRestService.getItemRestMap(writeOfItems, storage, time);
        return writeOffItemMap.entrySet().stream()
                .filter(entry -> itemRestMap.getOrDefault(entry.getKey(), BigDecimal.ZERO).compareTo(entry.getValue()) < 0)
                .map(entry -> {
                    BigDecimal required = entry.getValue();
                    BigDecimal rest = itemRestMap.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                    BigDecimal quantity = required.subtract(rest);
                    return new AbstractMap.SimpleImmutableEntry<>(entry.getKey(), quantity);})
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected Map<Item, BigDecimal> getItemMapFromCheckDocs(List<ItemDoc> checks) {
        return checks.stream()
                .flatMap(check -> docItemService.getItemsByDoc(check).stream())
                .collect(Collectors.toMap(
                        DocumentItem::getItem,
                        DocumentItem::getQuantity,
                        BigDecimal::add));
    }

    protected ItemDoc createReceiptDoc(Storage storage, Project project, Map<Item, BigDecimal> itemMap, LocalDateTime time) {
        if(itemMap == null || itemMap.isEmpty()) return null;
        ItemDoc doc = getReceiptDoc(storage, project, time);
        itemDocRepository.save(doc);
        Set<DocumentItem> docItems = itemMap.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) != 0)
                .map(entry -> {
                    DocumentItem item = new DocumentItem(
                            doc, entry.getKey(),
                            entry.getValue(),
                            itemRestService.getLastPriceOfItem(entry.getKey(), time));
                    saveDocumentItem(item);
                    return item;
                }).collect(Collectors.toSet());
        doc.setDocumentItems(docItems);
        return doc;
    }

    protected ItemDoc createWriteOffDocForChecks(Storage storage, Project project, Map<Item, BigDecimal> itemMap, LocalDateTime time) {
        ItemDoc doc = getWriteOffDoc(storage, project, time);
        itemDocRepository.save(doc);
        Set<DocumentItem> docItemSet = itemMap.entrySet().stream()
                .filter(entry -> !disabledItemIds.contains(entry.getKey().getId()))
                .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) != 0)
                .map(entry -> saveDocumentItem(new DocumentItem(doc, entry.getKey(), entry.getValue())))
                .collect(Collectors.toSet());
        doc.setDocumentItems(docItemSet);
        return doc;
    }

    protected DocumentItem saveDocumentItem(DocumentItem docItem) {
        docItemService.save(docItem);
        return docItem;
    }

    protected List<ItemDoc> getUnHoldenChecksByStorageAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        return documentService.getDocumentsByTypeAndStorageAndIsHold(DocumentType.CHECK_DOC, storage, false, from, to);
    }

    protected List<OrderDoc> getUnHoldenOrdersByProjectAndPeriod(Project project, LocalDateTime from, LocalDateTime to) {
        List<DocumentType> types = List.of(DocumentType.CREDIT_ORDER_DOC, DocumentType.WITHDRAW_ORDER_DOC);
        return documentService.getDocumentsByTypeInAndProjectAndIsHold(types, project, false, from, to);
    }

    protected void checkExistingAllProjectsDocs(List<Project> projects, LocalDateTime from, LocalDateTime to) {
        if(ignoreMissingDocs) return;
        projects.forEach(project -> {
            if(!documentRepository.existsByDocTypeAndProjectAndDateTimeBetween(DocumentType.CREDIT_ORDER_DOC, project, from, to)) {
                throw new WarningException(String.format(Constants.NO_DOCS_TO_HOLD_FROM_PROJECT_MESSAGE, project.getName()), ExceptionType.HOLD_EXCEPTION, null);
            }
        });
    }

    protected void checkExistingNotHoldenDocsBefore(LocalDateTime time) {
        time = time.withHour(CHECK_DOC_START_HOUR);
        if(checkUnHoldenDocksExists(time, true))
            throw new UnHoldenDocsException(Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE);
    }

    protected boolean checkUnHoldenDocksExists(LocalDateTime time, boolean before) {
        String subject = String.format(Constants.CHECKS_HOLDING_FAIL_SUBJECT, Util.getDateAndTime(LocalDateTime.now()));
        String message = before ?
                "Чеки не будут проведены. " + Constants.NOT_HOLDEN_DOCS_EXISTS_BEFORE_MESSAGE :
                String.format(Constants.CHECKS_HOLDING_FAIL_MESSAGE, Util.getDate(time));
        if(documentRepository.existsByDateTimeBeforeAndIsDeletedAndIsHold(time, false, false)) {
            mailService.send(toEmail, subject, message);
            return true;
        }
        return false;
    }

    protected ItemDoc getWriteOffDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc itemDocOfType = getItemDocOfType(DocumentType.WRITE_OFF_DOC, project, time);
        itemDocOfType.setStorageFrom(storage);
        return itemDocOfType;
    }

    protected ItemDoc getReceiptDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc itemDocOfType = getItemDocOfType(DocumentType.RECEIPT_DOC, project, time);
        itemDocOfType.setStorageTo(storage);
        return itemDocOfType;
    }

    protected ItemDoc getItemDocOfType(DocumentType docType, Project project, LocalDateTime time) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setNumber(documentService.getNextDocumentNumber(docType));
        itemDoc.setDateTime(time);
        itemDoc.setDocType(docType);
        itemDoc.setProject(project);
        itemDoc.setAuthor(systemUser);
        return itemDoc;
    }
}
