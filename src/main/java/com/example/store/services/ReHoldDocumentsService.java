package com.example.store.services;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReHoldDocumentsService {

    @Autowired
    private LotService lotService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private DocItemService docItemService;

    // TODO add tests

    public void checkReHoldingIsPossible(ItemDoc itemDoc, DocDTO docDTO) {
        if(!itemDoc.isHold()) return;
        Map<DocumentItem, Float> changedDocItemMap = getChangedDocItems(itemDoc, docDTO);
        Map<Item, Float> quantityShortageMap = getItemFloatMap(itemDoc, changedDocItemMap);
        if(!quantityShortageMap.isEmpty()) {
            String exceptionMessage = Constants.HOLD_FAILED_MESSAGE + " " +
                    quantityShortageMap.entrySet().stream()
                            .map(entry -> entry.getKey().getName() + " = " + entry.getValue())
                            .collect(Collectors.joining(", "));
            throw new HoldDocumentException(exceptionMessage);
        }
    }

    public void reHoldDocument(ItemDoc itemDoc) {
        if(!itemDoc.isHold()) return;
        System.out.println(itemDoc.getId());
    }

    private Map<Item, Float> getItemFloatMap(ItemDoc itemDoc, Map<DocumentItem, Float> changedDocItemMap) {
        Map<Item, Float> quantityShortageMap = new HashMap<>();
        for(Map.Entry<DocumentItem, Float> entry : changedDocItemMap.entrySet()) {
            DocumentItem docItem = entry.getKey();
            float quantityDiff;
            if(docItem.getItemDoc() != null) {
                Lot lot = lotService.getLotByDocumentItem(docItem);
                quantityDiff = entry.getValue()
                        - itemRestService.getRestOfLot(lot, itemDoc.getStorageFrom());
            } else {
                quantityDiff = entry.getValue()
                        - itemRestService.getRestOfItemOnStorage(
                        docItem.getItem(), itemDoc.getStorageFrom(), LocalDateTime.now());
            }
            if(quantityDiff < 0) {
                quantityShortageMap.put(docItem.getItem(), quantityDiff);
            }
        }
        return quantityShortageMap;
    }

    public Map<DocumentItem, Float> getChangedDocItems(ItemDoc itemDoc, DocDTO docDTO) {
        if(itemDoc.getStorageTo().getId() != docDTO.getStorageTo().getId()) {
            return getDocumentItemFloatMap(itemDoc);
        } else {
            List<DocItemDTO> itemDTOList = new ArrayList<>(docDTO.getDocItems());
            Map<DocumentItem, Float> map = new HashMap<>();
            for(DocumentItem docItem : itemDoc.getDocumentItems()) {
                Optional<DocItemDTO> itemDTO = contains(itemDTOList, docItem.getItem().getId());
                if(itemDTO.isPresent()) {
                    if(docItem.getQuantity() != itemDTO.get().getQuantity()) {
                        map.put(docItem, docItem.getQuantity() - itemDTO.get().getQuantity());
                        itemDTOList.remove(itemDTO.get());
                    }
                } else {
                    map.put(docItem, docItem.getQuantity());
                }
            }
            for(DocItemDTO itemDTO : itemDTOList) {
                map.put(docItemService.createNewDocItem(itemDTO), itemDTO.getQuantity());
            }
            return map;
        }
    }

    Optional<DocItemDTO> contains(List<DocItemDTO> list, int itemId) {
        return list.stream().filter(dto -> dto.getItemId() == itemId).findFirst();
    }

    private Map<DocumentItem, Float> getDocumentItemFloatMap(ItemDoc itemDoc) {
        return itemDoc.getDocumentItems().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        DocumentItem::getQuantity));
    }

}
