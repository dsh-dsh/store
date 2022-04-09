package com.example.store.services;

import com.example.store.factories.ItemDocFactory;
import com.example.store.model.dto.ItemQuantityPriceDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.ItemDocRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckHoldService {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ItemDocFactory itemDocFactory;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemDocRepository itemDocRepository;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private IngredientService ingredientService;

    @Scheduled(cron = "${hold.docs.scheduling.cron.expression}")
    public void holdChecksByPeriod(LocalDateTime from, LocalDateTime to) {
        List<Storage> storages = storageService.getStorageList();
        storages.forEach(storage -> holdChecksByStoragesAndPeriod(storage, from, to));
    }

    @Transactional
    private void holdChecksByStoragesAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {

        List<ItemDoc> checks = getUnHoldenChecksByStorageAndPeriod(storage, from, to);
        Project project = checks.get(0).getProject();

        Map<Item, Float> checksItemMap = getItemMapFromCheckDocs(checks);
        Map<Item, Float> writeOffItemMap = ingredientService.getIngredientMap(checksItemMap, to.toLocalDate());
        List<ItemQuantityPriceDTO> postingItemList = getPostingItemMap(writeOffItemMap, storage, to);

        ItemDoc postingDoc = createPostingDoc(storage, project, postingItemList);
        ItemDoc writeOffDoc = createWriteOffDocForChecks(storage, project, writeOffItemMap);

        if(itemDocFactory.holdDocument(postingDoc)) {
            if (itemDocFactory.holdDocument(writeOffDoc)) {
                checks.forEach(check -> documentService.setItemDocHolden(check));
            }
        }
    }

    private List<ItemQuantityPriceDTO> getPostingItemMap(Map<Item, Float> writeOffItemMap, Storage storage, LocalDateTime time) {
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

    private Map<Item, Float> getItemMapFromCheckDocs(List<ItemDoc> checks) {
        return checks.stream()
                .flatMap(check -> docItemService.getItemsByDoc(check).stream())
                .collect(Collectors.toMap(
                        DocumentItem::getItem,
                        DocumentItem::getQuantity,
                        Float::sum));
    }

    private ItemDoc createPostingDoc(Storage storage, Project project, List<ItemQuantityPriceDTO> dtoList) {
        ItemDoc postingDoc = getPostingDoc(storage, project);
        Set<DocumentItem> docItems = dtoList.stream().
                map(dto -> {
                    DocumentItem item = getDocumentItem(postingDoc, dto.getItem(), dto.getQuantity());
                    item.setPrice(dto.getPrice());
                    return item;
                }).collect(Collectors.toSet());
        postingDoc.setDocumentItems(docItems);
        return postingDoc;
    }

    private ItemDoc createWriteOffDocForChecks(Storage storage, Project project, Map<Item, Float> itemMap) {
        ItemDoc writeOffDoc = getWriteOffDoc(storage, project);
        Set<DocumentItem> docItemSet = itemMap.entrySet().stream()
                .map(entry -> getDocumentItem(writeOffDoc, entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
        writeOffDoc.setDocumentItems(docItemSet);
        return writeOffDoc;
    }

    @NotNull
    private DocumentItem getDocumentItem(ItemDoc writeOffDoc, Item item, float quantity) {
        DocumentItem docItem = new DocumentItem();
        docItem.setItemDoc(writeOffDoc);
        docItem.setItem(item);
        docItem.setQuantity(quantity);
        return docItem;
    }

    private List<ItemDoc> getUnHoldenChecksByStorageAndPeriod(Storage storage, LocalDateTime from, LocalDateTime to) {
        return documentService.getDocumentsByTypeAndStorageAndIsHold(DocumentType.CHECK_DOC, storage, false, from, to);
    }

    private ItemDoc getWriteOffDoc(Storage storage, Project project) {
        ItemDoc writeOffDoc = addItemDocOfType(DocumentType.WRITE_OFF_DOC, project);
        writeOffDoc.setStorageFrom(storage);
        itemDocRepository.save(writeOffDoc);
        return writeOffDoc;
    }

    private ItemDoc getPostingDoc(Storage storage, Project project) {
        ItemDoc postingDoc = addItemDocOfType(DocumentType.POSTING_DOC, project);
        postingDoc.setStorageTo(storage);
        itemDocRepository.save(postingDoc);
        return postingDoc;
    }

    private ItemDoc addItemDocOfType(DocumentType docType, Project project) {
        ItemDoc writeOffDoc = new ItemDoc();
        writeOffDoc.setNumber(itemDocRepository.getLastNumber(docType.toString()));
        writeOffDoc.setDateTime(LocalDateTime.now()); // TODO
        writeOffDoc.setDocType(docType);
        writeOffDoc.setProject(project);
        writeOffDoc.setAuthor(userService.getSystemAuthor());
        return writeOffDoc;
    }
}
