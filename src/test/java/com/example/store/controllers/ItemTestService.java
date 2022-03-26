package com.example.store.controllers;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.repositories.ItemRepository;
import com.example.store.services.PriceService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ItemTestService extends TestService{

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PriceService priceService;


    public Item getItemByName(String name, LocalDate date) {
        Item item = itemRepository.findByName(name)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_ITEM_MESSAGE));
        item.setPrices(priceService.getPriceListOfItem(item, date));
        return item;
    }

    public List<Price> getItemPriceList(Item item) {
        return priceService.getItemPriceList(item);
    }
}
