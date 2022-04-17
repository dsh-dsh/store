package com.example.store.services;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.factories.ItemDocFactory;
import com.example.store.factories.OrderDocFactory;
import com.example.store.model.dto.ItemQuantityPriceDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.ItemDocRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Service
public class Hold1CDocksService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ItemDocFactory itemDocFactory;
    @Autowired
    private OrderDocFactory orderDocFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private IngredientService ingredientService;

    private ItemDoc postingDoc, writeOffDoc;
    private List<ItemDoc> checks;

    // TODO test
    public void hold1CDocsByPeriod(LocalDateTime from, LocalDateTime to) {
        List<Storage> storages = storageService.getStorageList();
        storages.forEach(storage -> {
            createDocsToHoldByStoragesAndPeriod(storage, from, to);
            holdDocsAndChecksByStoragesAndPeriod(storage, from, to);
        });
        List<Project> projects = projectService.getProjectList();
        projects.forEach(project -> holdOrdersByProjectsAndPeriod(project, from, to));
    }

    // TODO test
    @Transactional
    public void createDocsToHoldByStoragesAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        checks = getUnHoldenChecksByStorageAndPeriod(storage, from, to);
        Project project = checks.get(0).getProject();

        Map<Item, Float> itemMap = getItemMapFromCheckDocs(checks);
        Map<Item, Float> writeOffItemMap = ingredientService.getIngredientMap(itemMap, to.toLocalDate());
        List<ItemQuantityPriceDTO> postingItemList = getPostingItemMap(writeOffItemMap, storage, to);

        postingDoc = createPostingDoc(storage, project, postingItemList, from);
        writeOffDoc = createWriteOffDocForChecks(storage, project, writeOffItemMap, from.plusSeconds(30L));
    }

    @Transactional
    public void holdDocsAndChecksByStoragesAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        if (postingDoc != null) {
            itemDocFactory.holdDocument(postingDoc);
        }
        itemDocFactory.holdDocument(writeOffDoc);
        checks.forEach(check -> documentService.setCheckDocHolden(check));

    }

    // TODO test
    @Transactional
    public void holdOrdersByProjectsAndPeriod(Project project, LocalDateTime from, LocalDateTime to) {
        List<OrderDoc> orders = getUnHoldenOrdersByProjectAndPeriod(project, from, to);
        for(OrderDoc order : orders) {
            orderDocFactory.holdDocument(order);
        }
    }

    public List<ItemQuantityPriceDTO> getPostingItemMap(Map<Item, Float> writeOffItemMap, Storage storage, LocalDateTime time) {
        Map<Item, Float> itemRestMap = itemRestService
                .getItemRestMap(writeOffItemMap, storage, time);
        return writeOffItemMap.entrySet().stream()
                .filter(entry -> itemRestMap.getOrDefault(entry.getKey(), 0f) < entry.getValue())
                .map(entry -> new ItemQuantityPriceDTO(
                        entry.getKey(),
                        entry.getValue() - itemRestMap.getOrDefault(entry.getKey(), 0f),
                        itemRestService.getLastPriceOfItem(entry.getKey())))
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
        ItemDoc postingDoc = getPostingDoc(storage, project, time);
        Set<DocumentItem> docItems = dtoList.stream().
                map(dto -> {
                    DocumentItem item = getDocumentItem(postingDoc, dto.getItem(), dto.getQuantity());
                    item.setPrice(dto.getPrice());
                    saveDocumentItem(item);
                    return item;
                }).collect(Collectors.toSet());
        postingDoc.setDocumentItems(docItems);
        itemDocRepository.save(postingDoc);
        return postingDoc;
    }

    public ItemDoc createWriteOffDocForChecks(Storage storage, Project project, Map<Item, Float> itemMap, LocalDateTime time) {
        ItemDoc writeOffDoc = getWriteOffDoc(storage, project, time);
        Set<DocumentItem> docItemSet = itemMap.entrySet().stream()
                .map(entry -> saveDocumentItem(getDocumentItem(writeOffDoc, entry.getKey(), entry.getValue())))
                .collect(Collectors.toSet());
        writeOffDoc.setDocumentItems(docItemSet);
        itemDocRepository.save(writeOffDoc);
        return writeOffDoc;
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
        List<DocumentType> types = List.of(DocumentType.CREDIT_ORDER_DOC, DocumentType.WITHDRAW_DOC_DOC);
        return documentService.getDocumentsByTypeInAndProjectAndIsHold(types, project, false, from, to);
    }

    public ItemDoc getWriteOffDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc writeOffDoc = getItemDocOfType(DocumentType.WRITE_OFF_DOC, project, time);
        writeOffDoc.setStorageFrom(storage);
        return writeOffDoc;
    }

    public ItemDoc getPostingDoc(Storage storage, Project project, LocalDateTime time) {
        ItemDoc postingDoc = getItemDocOfType(DocumentType.POSTING_DOC, project, time);
        postingDoc.setStorageTo(storage);
        return postingDoc;
    }

    public ItemDoc getItemDocOfType(DocumentType docType, Project project, LocalDateTime time) {
        ItemDoc itemDoc = new ItemDoc();
        itemDoc.setNumber(documentService.getNextDocumentNumber(docType));
        itemDoc.setDateTime(time);
        itemDoc.setDocType(docType);
        itemDoc.setProject(project);
        itemDoc.setAuthor(userService.getSystemAuthor());
        return itemDoc;
    }
}
