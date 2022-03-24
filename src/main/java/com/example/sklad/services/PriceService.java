package com.example.sklad.services;

import com.example.sklad.model.dto.PriceDTO;
import com.example.sklad.model.entities.Item;
import com.example.sklad.model.entities.Price;
import com.example.sklad.model.enums.PriceType;
import com.example.sklad.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public List<Price> getPriceListOfItem(Item item) {  // TODO переделать метод. Должен возвращать только последние цены всех типов
        return priceRepository.findByItem(item);
    }

    public Price setNewPrice(PriceDTO priceDTO, Item item) {
        Price price = new Price();
        price.setPriceType(PriceType.getByValue(priceDTO.getType()));
        price.setDate(LocalDate.now());
        price.setItem(item);
        price.setValue(priceDTO.getValue());
        priceRepository.save(price);

        return price;
    }


}
