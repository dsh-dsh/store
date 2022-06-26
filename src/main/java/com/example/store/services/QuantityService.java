package com.example.store.services;

import com.example.store.mappers.QuantityMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import com.example.store.repositories.QuantityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuantityService {

    @Autowired
    private QuantityRepository quantityRepository;
    @Autowired
    private QuantityMapper quantityMapper;

    public List<Quantity> getQuantityList(Ingredient ingredient, LocalDate date) {
        List<Quantity> list = new ArrayList<>();
        for(QuantityType type : QuantityType.values()) {
            Optional<Quantity> optionalQuantity = quantityRepository
                    .findFirstByIngredientAndDateLessThanEqualAndType(ingredient, date, type, Sort.by("date").descending());
            optionalQuantity.ifPresent(list::add);
        }
        return list;
    }

    public List<QuantityDTO> getQuantityDTOList(Ingredient ingredient, LocalDate date) {
        List<Quantity> quantities = getQuantityList(ingredient, date);
        return quantities.stream().map(quantityMapper::mapToDTO).collect(Collectors.toList());
    }

    public float getQuantityRatio(Ingredient ingredient, LocalDate date) {
        List<Quantity> quantities = getQuantityList(ingredient, date);
        Optional<Quantity> netQuantity = quantities.stream().
                filter(q -> q.getType().equals(QuantityType.NET)).findFirst();
        Optional<Quantity> grossQuantity = quantities.stream().
                filter(q -> q.getType().equals(QuantityType.GROSS)).findFirst();
        if(netQuantity.isEmpty() || grossQuantity.isEmpty()) return 0f;
        return netQuantity.get().getQuantity() / grossQuantity.get().getQuantity();
    }

    public Optional<Quantity> getGrossQuantity(Ingredient ingredient, LocalDate date) {
        return getQuantityList(ingredient, date).stream().
                filter(q -> q.getType().equals(QuantityType.GROSS))
                .findFirst();
    }

    public void setQuantities(Ingredient ingredient, IngredientDTO dto) {
        setQuantity(ingredient, dto.getNetto());
        setQuantity(ingredient, dto.getGross());
        setQuantity(ingredient, dto.getEnable());
    }

    private void setQuantity(Ingredient ingredient, QuantityDTO dto) {
        Quantity quantity = quantityMapper.mapToItem(dto);
        quantity.setIngredient(ingredient);
        quantityRepository.save(quantity);
    }

    public void softDeleteQuantities(Ingredient ingredient, LocalDate date) {
        List<Quantity> quantities = quantityRepository
                .findByIngredientAndDateLessThanEqual(ingredient, date, Sort.unsorted());
        quantities.forEach(quantity -> quantity.setDeleted(true));
        quantityRepository.saveAll(quantities);
    }

    public void updateQuantities(Ingredient ingredient, IngredientDTO dto) {
        updateQuantity(ingredient, dto.getNetto());
        updateQuantity(ingredient, dto.getGross());
        updateQuantity(ingredient, dto.getEnable());
    }

    private void updateQuantity(Ingredient ingredient, QuantityDTO dto) {
        QuantityType type = QuantityType.valueOf(dto.getType());
        LocalDate date = convertDate(dto.getDate());
        Optional<Quantity> optional = quantityRepository.findById(dto.getId());
//                = quantityRepository.findTop1ByTypeAndDateLessThanEqualOrderByDateDesc(type, date);
        if(optional.isPresent()) {
            Quantity quantity = optional.get();
            if(quantity.getDate().isEqual(date)) {
                quantity.setQuantity(dto.getQuantity());
                quantityRepository.save(quantity);
            } else {
                if(quantity.getQuantity() != dto.getQuantity()) {
                    setQuantity(ingredient, dto);
                }
            }
        } else {
            setQuantity(ingredient, dto);
        }
    }

    private LocalDate convertDate(long longTime) {
        return Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
