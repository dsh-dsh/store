package com.example.store.services;

import com.example.store.components.PeriodDateTime;
import com.example.store.components.SystemSettingsCash;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.DocItemMapper;
import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.dto.RestDTO;
import com.example.store.model.dto.StorageDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Period;
import com.example.store.model.entities.Storage;
import com.example.store.model.enums.ExceptionType;
import com.example.store.model.enums.SettingType;
import com.example.store.model.projections.LotBigDecimal;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.ItemRepository;
import com.example.store.repositories.LotRepository;
import com.example.store.repositories.PeriodRepository;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private ItemService itemService;
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
    private PeriodDateTime periodDateTime;
    @Autowired
    private PeriodService periodService;
    @Autowired
    private SystemSettingsCash systemSettingsCash;

    // todo refactor to BigDecimal all methods in class

    public void checkQuantityShortage(Item item, Map<Lot, BigDecimal> lotMap, BigDecimal docItemQuantity) {
        BigDecimal lotsQuantitySum = lotMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if(docItemQuantity.compareTo(lotsQuantitySum) > 0) {
            throw new BadRequestException(
                    String.format(Constants.SHORTAGE_OF_ITEM_MESSAGE, item.getName(), docItemQuantity, lotsQuantitySum),
                    ExceptionType.HOLD_EXCEPTION,
                    this.getClass().getName() + " checkQuantityShortage(Item item, Map<Lot, Float> lotMap, float docItemQuantity)");
        }
    }

    public List<DocItemDTO> getItemRest(int docId, long time, int storageId) {
        LocalDateTime dateTime = Util.getLocalDateTime(time);
        Storage storage = storageService.getById(storageId);
        List<Item> items = itemService.getIngredientItemsList(
                itemRepository.findByParentIds(List.of(systemSettingsCash.getProperty(SettingType.INGREDIENT_DIR_ID))));
        return items.stream()
                .map(item -> getDocItemDTO(docId, item, storage, dateTime))
                .filter(item -> item.getQuantity() != 0)
                .collect(Collectors.toList());
    }

    public LocalDateTime getPeriodDateTime() {
        Optional<Period> period = periodRepository.findByIsCurrent(true);
        return period.map(value -> value.getStartDate().atStartOfDay())
                .orElseGet(() -> LocalDate.parse(Constants.DEFAULT_PERIOD_START).atStartOfDay());
    }

    public DocItemDTO getDocItemDTO(int docId, Item item, Storage storage, LocalDateTime time) {
        DocItemDTO dto = new DocItemDTO();
        dto.setItemId(item.getId());
        dto.setItemName(item.getName());
        dto.setDocumentId(docId);
        dto.setQuantity(getRestOfItemOnStorage(item, storage, time).floatValue());
        dto.setPrice(getLastPriceOfItem(item, time));
        dto.setAmount(dto.getQuantity()*dto.getPrice());
        return dto;
    }

    public Map<Item, BigDecimal> getItemRestMap(List<Item> itemList, Storage storage, LocalDateTime time) {
        return itemList.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestOfItemOnStorage(item, storage, time)));
    }

    // todo try to refactor it, start from retrieving all lot_moves of period, then collect it to item list with rests
    public Map<Item, BigDecimal> getItemsRestOnStorage(List<Item> items, Storage storage, LocalDateTime time) {
        return items.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestOfItemOnStorageOutOfPeriod(item, storage, time)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // todo try to refactor it, start from retrieving all lot_moves of period, then collect it to item list with rests
    public Map<Item, BigDecimal> getItemsRestOnStorage(Storage storage, LocalDateTime time) {
        List<Item> items = itemService.getIngredientItemsList(
                itemRepository.findByParentIds(List.of(systemSettingsCash.getProperty(SettingType.INGREDIENT_DIR_ID))));
        return items.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestOfItemOnStorageOutOfPeriod(item, storage, time)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // todo try to refactor it, start from retrieving all lot_moves of period, then collect it to item list with rests
    public Map<Item, RestPriceValue> getItemsRestOnStorageForClosingPeriod(Storage storage, LocalDateTime time) {
        List<Item> items = itemService.getIngredientItemsList(
                itemRepository.findByParentIds(List.of(systemSettingsCash.getProperty(SettingType.INGREDIENT_DIR_ID))));
        return items.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> getRestAndPriceForClosingPeriod(item, storage, time)))
                .entrySet().stream()
                .filter(entry -> entry.getValue().getRest().floatValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    protected RestPriceValue getRestAndPriceForClosingPeriod(Item item, Storage storage, LocalDateTime time) {
        BigDecimal rest = getRestOfItemOnStorage(item, storage, time);
        float price = systemSettingsCash.getProperty(SettingType.PERIOD_AVERAGE_PRICE) == 1 ?
                getAveragePriceOfItem(item, storage, time, rest.floatValue()) :
                getLastPriceOfItem(item, time);
        return new RestPriceValue(rest, price);
    }

    public float getAveragePriceOfItem(Item item, Storage storage, LocalDateTime time, float quantity) {
        if(quantity == 0) return 0;
        float amount = (float) lotRepository
                .getLotAmountOfItem(item.getId(), storage.getId(), periodDateTime.getStartDateTime(), time)
                .stream().mapToDouble(LotFloat::getValue).sum();
        return amount/quantity;
    }

    public float getLastPriceOfItem(Item item, LocalDateTime time) {
        Float value = lotRepository.findLastPrice(item.getId(), time);
        return value == null ? 0 : value;
    }

    public BigDecimal getRestOfItemOnStorage(Item item, Storage storage, LocalDateTime docTime) {
        return lotRepository.getLotsOfItem(item.getId(), storage.getId(), periodDateTime.getStartDateTime(), docTime)
                .stream()
                .map(LotBigDecimal::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getRestOfItemOnStorageOutOfPeriod(Item item, Storage storage, LocalDateTime docTime) {
        LocalDateTime periodStart = periodService.getStartDateByDateInPeriod(docTime.toLocalDate()).atStartOfDay();
        return lotRepository.getLotsOfItem(item.getId(), storage.getId(), periodStart, docTime)
                .stream()
                .map(LotBigDecimal::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public float getRestOfLot(Lot lot, Storage storage) {
        return lotRepository.getQuantityRestOfLot(lot.getId(), storage.getId());
    }

    public List<RestDTO> getItemRestList(Item item, LocalDateTime dateTime) {
        List<Storage> storages = storageService.getStorageList(dateTime.toLocalDate());
        return storages.stream()
                .map(storage -> new RestDTO(
                        new StorageDTO(storage),
                        getRestOfItemOnStorage(item, storage, dateTime).floatValue()))
                .collect(Collectors.toList());
    }

    class RestPriceValue {
        private BigDecimal rest;
        private float price;
        public RestPriceValue(BigDecimal rest, float price) {
            this.rest = rest;
            this.price = price;
        }
        public BigDecimal getRest() {
            return rest;
        }
        public float getPrice() {
            return price;
        }
    }
}


