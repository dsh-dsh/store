package com.example.store.services;

import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import com.example.store.repositories.PriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public List<Price> getPriceListOfItem(Item item) {
        return Arrays.stream(PriceType.values())
                .map(type -> getPriceByItemAndType(item, type))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Price getPriceByItemAndType(Item item, PriceType type) {
        Pageable pageable =
                PageRequest.of(0,1, Sort.by("date").descending());
        List<Price> prices = priceRepository.findByItemAndPriceType(item, type, pageable);
        return prices.size() == 1 ? prices.get(0) : null;
    }

    public Price setNewPrice(PriceDTO priceDTO, Item item) {
        Price price = new Price();
        price.setPriceType(PriceType.getByValue(priceDTO.getType()));
        LocalDate date = priceDTO.getDate() == null ? LocalDate.now() : LocalDate.parse(priceDTO.getDate());
        price.setDate(date);
        price.setItem(item);
        price.setValue(priceDTO.getValue());
        priceRepository.save(price);

        return price;
    }


}
