package com.example.store.components;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.services.DocItemService;
import com.example.store.services.ItemRestService;
import com.example.store.services.LotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ReHoldChecking {

    @Autowired
    private LotService lotService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private DocItemService docItemService;

    public boolean checkPossibility(ItemDoc itemDoc, DocDTO docDTO) {
        if(!itemDoc.isHold()) return false;
        if(itemDoc.getStorageTo().getId() != docDTO.getStorageTo().getId()) return false;
        if(!itemDoc.getDateTime().equals(LocalDateTime.parse(docDTO.getTime()))) return false;
        if(itemDoc.getDocType() == DocumentType.POSTING_DOC || itemDoc.getDocType() == DocumentType.RECEIPT_DOC) {
            Map<DocumentItem, Float> changedDocItemMap = new HashMap<>();
            if(!setChangedDocItems(changedDocItemMap, itemDoc, docDTO)) return false;
            Map<Item, Float> quantityDiffMap = getQuantityDiffMap(itemDoc, changedDocItemMap);
            return !quantityDiffMap.isEmpty();
        } else {
            return false;
        }
    }

    public Map<Item, Float> getQuantityDiffMap(ItemDoc itemDoc, Map<DocumentItem, Float> changedDocItemMap) {
        Map<Item, Float> quantityDiffMap = new HashMap<>();
        setQuantityDiffMap(quantityDiffMap, itemDoc.getStorageTo(), changedDocItemMap);
        return quantityDiffMap;
    }

    public void setQuantityDiffMap(Map<Item, Float> quantityDiffMap, Storage storage,
            Map<DocumentItem, Float> changedDocItemMap) {

        for(Map.Entry<DocumentItem, Float> entry : changedDocItemMap.entrySet()) {
            DocumentItem docItem = entry.getKey();
            float quantityDiff;
            if(docItem.getItemDoc() != null) {
                Lot lot = lotService.getLotByDocumentItemForPosting(docItem);
                float rest = itemRestService.getRestOfLot(lot, storage);
                quantityDiff = entry.getValue() + rest;
            } else {
                quantityDiff = entry.getValue() + itemRestService
                        .getRestOfItemOnStorage(docItem.getItem(), storage, LocalDateTime.now());
            }
            if(quantityDiff > 0) {
                quantityDiffMap.put(docItem.getItem(), quantityDiff);
            }
        }
    }

    public boolean setChangedDocItems(Map<DocumentItem, Float> map, ItemDoc itemDoc, DocDTO docDTO) {
        List<DocItemDTO> itemDTOList = new ArrayList<>(docDTO.getDocItems());
        for (DocumentItem docItem : itemDoc.getDocumentItems()) {
            Optional<DocItemDTO> itemDTO = findDocItemDTOByItemId(itemDTOList, docItem.getItem().getId());
            if (itemDTO.isPresent()) {
                if (docItem.getQuantity() <= itemDTO.get().getQuantity()) {
                    map.put(docItem, itemDTO.get().getQuantity() - docItem.getQuantity());
                    itemDTOList.remove(itemDTO.get());
                }
            } else {
                return false;
            }
        }
        return itemDTOList.isEmpty();
    }

    public Optional<DocItemDTO> findDocItemDTOByItemId(List<DocItemDTO> list, int itemId) {
        return list.stream().filter(dto -> dto.getItemId() == itemId).findFirst();
    }

}
