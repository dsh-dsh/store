package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.enums.ExceptionType;
import com.example.store.mappers.DocItemMapper;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.RestDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.ItemRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemRestService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LotRepository lotRepository;
    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private DocItemMapper docItemMapper;
    @Autowired
    private StorageService storageService;

    public void checkQuantityShortage(Map<Lot, Float> lotMap, float docItemQuantity) {
        double lotsQuantitySum = lotMap.values().stream().mapToDouble(d -> d).sum();
        if(docItemQuantity > lotsQuantitySum)
            throw new BadRequestException(Constants.SHORTAGE_OF_ITEM_MESSAGE,
                    ExceptionType.HOLD_EXCEPTION);
    }

    public List<DocItemDTO> getItemRest(int docId, long time, int storageId) {
        LocalDateTime dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
        Storage storage = storageService.getById(storageId);
        List<Item> items = itemRepository.findByParentIds(Constants.INGREDIENTS_PARENT_IDS);
        return items.stream()
                .map(item -> getDocItemDTO(docId, item, storage, dateTime))
                .collect(Collectors.toList());
    }

    public DocItemDTO getDocItemDTO(int docId, Item item, Storage storage, LocalDateTime time) {
        DocItemDTO dto = new DocItemDTO();
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setDocumentId(docId);
        dto.setQuantity(getRestOfItemOnStorage(item, storage, time));
        dto.setPrice(getLastPriceOfItem(item));
        dto.setAmount(dto.getQuantity()*dto.getPrice());
        return dto;
    }

    public Map<Item, Float> getItemRestMap(Map<Item, Float> itemMap, Storage storage, LocalDateTime time) {
        return itemMap.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> (float) lotRepository
                                .getLotsOfItem(item.getId(), storage.getId(), time).stream()
                                .mapToDouble(LotFloat::getValue).sum()));
    }

    public float getLastPriceOfItem(Item item) {
        Float value = lotRepository.findLastPrice(item.getId());
        return value == null ? 0 : value;
    }

    public float getRestOfItemOnStorage(Item item, Storage storage, LocalDateTime time) {
        return (float) lotRepository
                .getLotsOfItem(item.getId(), storage.getId(), time)
                .stream()
                .mapToDouble(LotFloat::getValue).sum();
    }

    public float getRestOfLot(Lot lot, Storage storage) {
        return lotRepository.getQuantityRestOfLot(lot.getId(), storage.getId());
    }

    public List<RestDTO> getItemRestList(Item item) {
        LocalDateTime now = LocalDateTime.now();
        List<Storage> storages = storageService.getStorageList();
        return storages.stream()
                .map(storage -> new RestDTO(
                        new StorageDTO(storage),
                        getRestOfItemOnStorage(item, storage, now)))
                .collect(Collectors.toList());
    }
}


