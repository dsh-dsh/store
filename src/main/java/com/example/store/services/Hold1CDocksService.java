package com.example.store.services;

import com.example.store.model.dto.ItemQuantityPriceDTO;
import com.example.store.model.entities.*;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.ItemDocRepository;
import com.example.store.repositories.OrderDocRepository;
import com.example.store.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class Hold1CDocksService {

    public static final boolean BY_CARD_KEY = true;
    public static final boolean BY_CASH_KEY = false;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private ProjectService projectService;
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
    private List<PropertySetting> systemSettings;
    @Autowired
    private User systemUser;

    private ItemDoc postingDoc;
    private ItemDoc writeOffDoc;
    private List<ItemDoc> checks;

    private boolean addRestForHold = true;

    @PostConstruct
    protected void setSettings() {
        PropertySetting setting = PropertySetting.getByType(systemSettings, SettingType.ADD_REST_FOR_HOLD);
        if(setting != null) {
            addRestForHold = setting.getProperty() == 1;
        }
    }

    public void createSaleOrders(Storage storage, LocalDateTime time) {
        Optional<Project> optionalProject = projectService.getProjectByStorageName(storage.getName());
        Project project;
        if(optionalProject.isEmpty()) return;
        else project = optionalProject.get();

        Map<Boolean, Float> sumMap = getSumMap();
        if(sumMap.get(BY_CARD_KEY) > 0) {
            createOrderDoc(sumMap.get(BY_CARD_KEY), PaymentType.SALE_CARD_PAYMENT, project, time);
        }
        if(sumMap.get(BY_CASH_KEY) > 0) {
            createOrderDoc(sumMap.get(BY_CASH_KEY), PaymentType.SALE_CASH_PAYMENT, project, time);
        }
    }

    public void createOrderDoc(float sum, PaymentType type, Project project, LocalDateTime time) {
        DocumentType docType = DocumentType.CREDIT_ORDER_DOC;
        OrderDoc order = new OrderDoc();
        order.setNumber(documentService.getNextDocumentNumber(docType));
        order.setDateTime(time);
        order.setDocType(docType);
        order.setProject(project);
        order.setAuthor(systemUser);
        order.setRecipient(companyService.getOurCompany());
        order.setPayed(true);
        order.setBaseDocument(writeOffDoc);
        order.setPaymentType(type);
        order.setAmount(sum);
        orderDocRepository.save(order);
    }

    public Map<Boolean, Float> getSumMap() {
        Map<Boolean, Float> sumMap = new HashMap<>();
        sumMap.put(true, 0f);
        sumMap.put(false, 0f);
        List<DocumentItem> items;
        CheckInfo checkInfo;
        for(ItemDoc check : checks) {
            items = docItemService.getItemsByDoc(check);
            checkInfo = checkInfoService.getCheckInfo(check);
            float sum = (float)items.stream().mapToDouble(item -> (item.getQuantity() * item.getPrice()) - item.getDiscount()).sum();
            sumMap.merge(checkInfo.isPayedByCard(), sum, Float::sum);
        }
        return sumMap;
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
                .forEach(doc -> holdDocsService.holdDocument(doc));
    }

    @Transactional
    public void createDocsToHoldByStoragesAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {

        holdDocsBefore();

        checks = getUnHoldenChecksByStorageAndPeriod(storage, from, to);
        if(checks.isEmpty()) return;

        Project project = checks.get(0).getProject();

        Map<Item, Float> itemMap = getItemMapFromCheckDocs(checks);
        Map<Item, Float> writeOffItemMap = ingredientService.getIngredientQuantityMap(itemMap, to.toLocalDate());
        writeOffDoc = createWriteOffDocForChecks(storage, project, writeOffItemMap, from.plusSeconds(30L));

        if(addRestForHold) {
            List<ItemQuantityPriceDTO> postingItemList = getPostingItemMap(writeOffItemMap, storage, to);
            postingDoc = createPostingDoc(storage, project, postingItemList, from);
        }
    }

    @Transactional
    public void holdDocsAndChecksByStoragesAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        if (postingDoc != null) {
            holdDocsService.holdDoc(postingDoc);
        }
        if(writeOffDoc != null) {
            holdDocsService.holdDoc(writeOffDoc);
            checks.forEach(check -> documentService.setHoldAndSave(true, check));
        }
    }

    @Transactional
    public void holdOrdersByProjectsAndPeriod(Project project, LocalDateTime from, LocalDateTime to) {
        List<OrderDoc> orders = getUnHoldenOrdersByProjectAndPeriod(project, from, to);
        for(OrderDoc order : orders) {
            holdDocsService.holdDoc(order);
        }
    }

    public List<ItemQuantityPriceDTO> getPostingItemMap(Map<Item, Float> writeOffItemMap, Storage storage, LocalDateTime time) {
        ///
        Map<Item, Float> itemRestMap = itemRestService
                .getItemRestMap(writeOffItemMap, storage, time);
        return writeOffItemMap.entrySet().stream()
                .filter(entry -> itemRestMap.getOrDefault(entry.getKey(), 0f) < entry.getValue())
                .map(entry -> new ItemQuantityPriceDTO(
                        entry.getKey(),
                        entry.getValue() - itemRestMap.getOrDefault(entry.getKey(), 0f),
                        itemRestService.getLastPriceOfItem(entry.getKey(), time)))
                .collect(Collectors.toList());
    }

    public Map<Item, Float> getItemMapFromCheckDocs(List<ItemDoc> checks) {
        return checks.stream()
                .flatMap(check -> docItemService.getItemsByDoc(check).stream())
                .collect(Collectors.toMap(
                        DocumentItem::getItem,
                        DocumentItem::getQuantity,
                        Float::sum));
    }

    public ItemDoc createPostingDoc(Storage storage, Project project, List<ItemQuantityPriceDTO> dtoList, LocalDateTime time) {
        if(dtoList == null || dtoList.isEmpty()) return null;
        ItemDoc doc = getPostingDoc(storage, project, time);
        Set<DocumentItem> docItems = dtoList.stream().
                map(dto -> {
                    DocumentItem item = getDocumentItem(doc, dto.getItem(), dto.getQuantity());
                    item.setPrice(dto.getPrice());
                    saveDocumentItem(item);
                    return item;
                }).collect(Collectors.toSet());
        doc.setDocumentItems(docItems);
        doc.setBaseDocument(writeOffDoc);
        itemDocRepository.save(doc);
        return doc;
    }

    public ItemDoc createWriteOffDocForChecks(Storage storage, Project project, Map<Item, Float> itemMap, LocalDateTime time) {
        ItemDoc doc = getWriteOffDoc(storage, project, time);
        Set<DocumentItem> docItemSet = itemMap.entrySet().stream()
                .map(entry -> saveDocumentItem(getDocumentItem(doc, entry.getKey(), entry.getValue())))
                .collect(Collectors.toSet());
        doc.setDocumentItems(docItemSet);
        itemDocRepository.save(doc);
        return doc;
    }

    public DocumentItem getDocumentItem(ItemDoc itemDoc, Item item, float quantity) {
        DocumentItem docItem = new DocumentItem();
        docItem.setItemDoc(itemDoc);
        docItem.setItem(item);
        docItem.setQuantity(quantity);
        return docItem;
    }

    public DocumentItem saveDocumentItem(DocumentItem docItem) {
        docItemService.save(docItem);
        return docItem;
    }

    public List<ItemDoc> getUnHoldenChecksByStorageAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        return documentService.getDocumentsByTypeAndStorageAndIsHold(DocumentType.CHECK_DOC, storage, false, from, to);
    }

    public List<OrderDoc> getUnHoldenOrdersByProjectAndPeriod(Project project, LocalDateTime from, LocalDateTime to) {
        List<DocumentType> types = List.of(DocumentType.CREDIT_ORDER_DOC, DocumentType.WITHDRAW_ORDER_DOC);
        return documentService.getDocumentsByTypeInAndProjectAndIsHold(types, project, false, from, to);
    }

    public ItemDoc getWriteOffDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc itemDocOfType = getItemDocOfType(DocumentType.WRITE_OFF_DOC, project, time);
        itemDocOfType.setStorageFrom(storage);
        return itemDocOfType;
    }

    public ItemDoc getPostingDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc itemDocOfType = getItemDocOfType(DocumentType.POSTING_DOC, project, time);
        itemDocOfType.setStorageTo(storage);
        return itemDocOfType;
    }

    public ItemDoc getItemDocOfType(DocumentType docType, Project project, LocalDateTime time) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setNumber(documentService.getNextDocumentNumber(docType));
        itemDoc.setDateTime(time);
        itemDoc.setDocType(docType);
        itemDoc.setProject(project);
        itemDoc.setAuthor(systemUser);
        return itemDoc;
    }
}
