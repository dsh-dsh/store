package com.example.store.controllers;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.repositories.ItemRepository;
import com.example.store.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
public class ItemTestService extends TestService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PriceService priceService;


    public Item getItemByName(String name, long longDate) {
        LocalDate date = Instant.ofEpochMilli(longDate).atZone(ZoneId.systemDefault()).toLocalDate();
        Item item = itemRepository.getByName(name);
        if(item != null) {
            item.setPrices(priceService.getPriceListOfItem(item, date));
        }
        return item;
    }

    public List<Price> getItemPriceList(Item item) {
        return priceService.getItemPriceList(item);
    }
}
