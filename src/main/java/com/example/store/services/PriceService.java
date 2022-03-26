package com.example.store.services;

import com.example.store.model.dto.ItemDTO;
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

    public void updateItemPrices(Item item, List<PriceDTO> priceDTOList, LocalDate date) {
        List<Price> priceListOnDate = getPriceListOfItem(item, date);
        priceDTOList.forEach(priceDTO -> updatePrice(priceDTO, priceListOnDate, item));
    }

    public List<Price> getPriceListOfItem(Item item, LocalDate date) {
        return Arrays.stream(PriceType.values())
                .map(type -> getPriceByItemAndType(item, type, date))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Price> getItemPriceList(Item item) {
        return priceRepository.findByItem(item);
    }


    private void updatePrice(PriceDTO dto, List<Price> prices, Item item) {
        Price price = getPriceOfType(prices, PriceType.getByValue(dto.getType()));
        if (price == null) {
            setNewPrice(item, dto);
            return;
        }
        if (LocalDate.parse(dto.getDate()).isAfter(price.getDate())) {
            if (price.getValue() != dto.getValue()) {
                setNewPrice(item, dto);
            }
        }
        if (LocalDate.parse(dto.getDate()).isEqual(price.getDate())){
            price.setValue(dto.getValue());
            priceRepository.save(price);
        }
    }

    private Price getPriceOfType(List<Price> prices, PriceType type) {
        for (Price price : prices) {
            if (price.getPriceType() == type) {
                return price;
            }
        }
        return null;
    }

    private Price getPriceByItemAndType(Item item, PriceType type, LocalDate date) {
        Pageable pageable =
                PageRequest.of(0,1, Sort.by("date").descending());
        List<Price> prices = priceRepository.findByItemAndPriceTypeAndDateLessThanEqual(item, type, date, pageable);
        return prices.size() == 1 ? prices.get(0) : null;
    }

    public void addPrices(Item item, ItemDTO itemDTO) {
        itemDTO.getPrices()
                .forEach(priceDTO -> setNewPrice(item, priceDTO));
    }

    private void setNewPrice(Item item, PriceDTO dto) {
        Price price = new Price();
        price.setPriceType(PriceType.getByValue(dto.getType()));
        LocalDate date = dto.getDate() == null ? LocalDate.now() : LocalDate.parse(dto.getDate());
        price.setDate(date);
        price.setItem(item);
        price.setValue(dto.getValue());
        priceRepository.save(price);
    }


}
