package com.example.store.services;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import com.example.store.repositories.PriceRepository;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public List<Price> getPriceListOfItem(Item item, LocalDate date) {
        return Arrays.stream(PriceType.values())
                .map(type -> getPriceByItemAndType(item, type, date))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Price> getItemPriceList(Item item) {
        return priceRepository.findByItem(item);
    }


    public void updateItemPrices(Item item, List<PriceDTO> priceDTOList, LocalDate date) {
        List<Price> priceListOnDate = getPriceListOfItem(item, date);
        priceDTOList.forEach(priceDTO -> updatePrice(priceDTO, priceListOnDate, item));
    }

    protected void updatePrice(PriceDTO dto, List<Price> prices, Item item) {
        Price price = getPriceOfType(prices, PriceType.valueOf(dto.getType()));
        if (price == null) {
            setNewPrice(item, dto);
            return;
        }
        LocalDate date = Util.getLocalDate(dto.getDate());
        if (date.isAfter(price.getDate())
                && price.getValue() != dto.getValue()) {
                setNewPrice(item, dto);
        }
        if (date.isEqual(price.getDate())){
            price.setValue(dto.getValue());
            priceRepository.save(price);
        }
    }

    protected Price getPriceOfType(List<Price> prices, PriceType type) {
        for (Price price : prices) {
            if (price.getPriceType() == type) {
                return price;
            }
        }
        return null;
    }

    protected Price getPriceByItemAndType(Item item, PriceType type, LocalDate date) {
        Optional<Price> price = priceRepository.findFirstByItemAndPriceTypeAndDateLessThanEqualOrderByDateDesc(item, type, date);
        return price.orElse(null);
    }

    public void addPrices(Item item, ItemDTO itemDTO) {
        itemDTO.getPrices()
                .forEach(priceDTO -> setNewPrice(item, priceDTO));
    }

    protected void setNewPrice(Item item, PriceDTO dto) {
        Price price = new Price();
        price.setPriceType(PriceType.valueOf(dto.getType()));
        LocalDate date = Util.getLocalDate(dto.getDate());
        price.setDate(date);
        price.setItem(item);
        price.setValue(dto.getValue());
        priceRepository.save(price);
    }


}
