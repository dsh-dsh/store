package com.example.store.services;

import com.example.store.components.EnvironmentVars;
import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.*;
import com.example.store.model.enums.ExceptionType;
import com.example.store.mappers.DocItemMapper;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.RestDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.ItemRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
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
    @Autowired
    private PeriodRepository periodRepository;
    @Autowired
    private EnvironmentVars env;
    @Autowired
    @Qualifier("periodAveragePrice")
    private PropertySetting periodAveragePriceSetting;
    @Autowired
    @Qualifier("ingredientDir")
    private PropertySetting ingredientDirSetting;

    public void checkQuantityShortage(Map<Lot, Float> lotMap, float docItemQuantity) {
        double lotsQuantitySum = lotMap.values().stream().mapToDouble(d -> d).sum();
        if(docItemQuantity > lotsQuantitySum) {
            String itemName = "";
            Optional<Lot> lot = lotMap.keySet().stream().findFirst();
            if(lot.isPresent()) {
                itemName = lot.get().getDocumentItem().getItem().getName();
            }
            throw new BadRequestException(
                    String.format(Constants.SHORTAGE_OF_ITEM_MESSAGE, itemName, docItemQuantity, lotsQuantitySum),
                    ExceptionType.HOLD_EXCEPTION);
        }
    }

    public List<DocItemDTO> getItemRest(int docId, long time, int storageId) {
        LocalDateTime dateTime = Util.getLocalDateTime(time);
        Storage storage = storageService.getById(storageId);
        List<Item> items = itemRepository.findByParentIds(List.of(ingredientDirSetting.getProperty()));
        return items.stream()
                .map(item -> getDocItemDTO(docId, item, storage, dateTime))
                .collect(Collectors.toList());
    }

    public LocalDateTime getPeriodStart() {
        Optional<Period> period = periodRepository.findByIsCurrent(true);
        return period.map(value -> value.getStartDate().atStartOfDay())
                .orElseGet(() -> LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay());
    }

    public DocItemDTO getDocItemDTO(int docId, Item item, Storage storage, LocalDateTime time) {
        DocItemDTO dto = new DocItemDTO();
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setDocumentId(docId);
        dto.setQuantity(getRestOfItemOnStorage(item, storage, time));
        dto.setPrice(getLastPriceOfItem(item, time));
        dto.setAmount(dto.getQuantity()*dto.getPrice());
        return dto;
    }

    public Map<Item, Float> getItemRestMap(Map<Item, Float> itemMap, Storage storage, LocalDateTime time) {
        return itemMap.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestOfItemOnStorage(item, storage, time)));
    }

    public Map<Item, RestPriceValue> getItemsRestOnStorageForClosingPeriod(Storage storage, LocalDateTime time) {
        List<Item> items = itemRepository.findByParentIds(List.of(ingredientDirSetting.getProperty()));
        return items.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestAndPriceForClosingPeriod(item, storage, time)))
                .entrySet().stream()
                .filter(entry -> entry.getValue().getRest() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected RestPriceValue getRestAndPriceForClosingPeriod(Item item, Storage storage, LocalDateTime time) {
        float rest = getRestOfItemOnStorage(item, storage, time);
        float price = periodAveragePriceSetting.getProperty() == 1 ?
                getAveragePriceOfItem(item, storage, time, rest) :
                getLastPriceOfItem(item, time);
        return new RestPriceValue(rest, price);
    }

    public float getAveragePriceOfItem(Item item, Storage storage, LocalDateTime time, float quantity) {
        if(quantity == 0) return 0;
        float amount = (float) lotRepository
                .getLotAmountOfItem(item.getId(), storage.getId(), env.getPeriodStart(), time)
                .stream().mapToDouble(LotFloat::getValue).sum();
        return amount/quantity;
    }

    public float getLastPriceOfItem(Item item, LocalDateTime time) {
        Float value = lotRepository.findLastPrice(item.getId(), time);
        return value == null ? 0 : value;
    }

    public float getRestOfItemOnStorage(Item item, Storage storage, LocalDateTime docTime) {
        return (float) lotRepository
                .getLotsOfItem(item.getId(), storage.getId(), env.getPeriodStart(), docTime)
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

    public List<RestDTO> getItemRestList(Item item, LocalDateTime dateTime) {
        List<Storage> storages = storageService.getStorageList();
        return storages.stream()
                .map(storage -> new RestDTO(
                        new StorageDTO(storage),
                        getRestOfItemOnStorage(item, storage, dateTime)))
                .collect(Collectors.toList());
    }

    class RestPriceValue {
        private float rest;
        private float price;
        public RestPriceValue(float rest, float price) {
            this.rest = rest;
            this.price = price;
        }
        public float getRest() {
            return rest;
        }
        public float getPrice() {
            return price;
        }
    }
}


