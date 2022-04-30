package com.example.store.services;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.Storage;
import com.example.store.model.projections.LotFloat;
import com.example.store.repositories.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemRestService {

    @Autowired
    private LotRepository lotRepository;

    public Map<Item, Float> getItemRestMap(Map<Item, Float> itemMap, Storage storage, LocalDateTime time) {
        return itemMap.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        item -> (float) lotRepository
                                .getLotsOfItem(item.getId(), storage.getId(), time).stream()
                                .mapToDouble(LotFloat::getValue).sum()));
    }

    public float getLastPriceOfItem(Item item) {
        return lotRepository.findLastPrice(item.getId());
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
}
