package com.example.store.services;

import com.example.store.components.PeriodicValuesCache;
import com.example.store.mappers.PeriodicValueMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.repositories.PeriodicValueRepository;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PeriodicValueService {

    @Autowired
    protected PeriodicValueRepository periodicValueRepository;
    @Autowired
    private PeriodicValueMapper periodicValueMapper;
    @Autowired
    PeriodicValuesCache periodicValuesCache;

    public List<PeriodicValue> getQuantityList(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> list = new ArrayList<>();
        for(PeriodicValueType type : PeriodicValueType.values()) {
            Optional<PeriodicValue> optionalQuantity = periodicValueRepository
                    .findFirstByIngredientAndDateLessThanEqualAndType(ingredient, date, type, Sort.by("date").descending());
            optionalQuantity.ifPresent(list::add);
        }
        return list;
    }

    public List<PeriodicValueDTO> getPeriodicValueDTOList(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> quantities = getQuantityList(ingredient, date);
        return quantities.stream().map(periodicValueMapper::mapToDTO).collect(Collectors.toList());
    }

    // todo add tests
    public float getGrossQuantity(Ingredient ingredient) {
        return periodicValuesCache.getGrossQuantities()
                .getOrDefault(ingredient.getId(), 0f);
    }

    // todo add tests
    public float getNetQuantity(Ingredient ingredient) {
        return periodicValuesCache.getNetQuantities()
                .getOrDefault(ingredient.getId(), 0f);
    }

    // todo add tests
    public float getEnableQuantity(Ingredient ingredient) {
        return periodicValuesCache.getEnableValues()
                .getOrDefault(ingredient.getId(), 0f);
    }

    public Optional<PeriodicValue> getNetQuantity(Ingredient ingredient, LocalDate date) {
        return getQuantityList(ingredient, date).stream().
                filter(q -> q.getType().equals(PeriodicValueType.NET))
                .findFirst();
    }

    public void setQuantities(Ingredient ingredient, IngredientDTO dto) {
        setQuantity(ingredient, dto.getNetto());
        setQuantity(ingredient, dto.getGross());
        setQuantity(ingredient, dto.getEnable());
    }

    protected void setQuantity(Ingredient ingredient, PeriodicValueDTO dto) {
        PeriodicValue periodicValue = periodicValueMapper.mapToItem(dto);
        periodicValue.setId(0);
        periodicValue.setIngredient(ingredient);
        periodicValueRepository.save(periodicValue);
    }

    public void softDeleteQuantities(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> quantities = periodicValueRepository
                .findByIngredientAndDateLessThanEqual(ingredient, date, Sort.unsorted());
        quantities.forEach(quantity -> quantity.setDeleted(true));
        periodicValueRepository.saveAll(quantities);
    }

    public void updateQuantities(Ingredient ingredient, IngredientDTO dto) {
        updateQuantity(ingredient, dto.getNetto());
        updateQuantity(ingredient, dto.getGross());
        updateQuantity(ingredient, dto.getEnable());
    }

    protected void updateQuantity(Ingredient ingredient, PeriodicValueDTO dto) {
        LocalDate date = Util.getLocalDate(dto.getDate());
        Optional<PeriodicValue> optional = periodicValueRepository.findById(dto.getId());
        if(optional.isPresent()) {
            PeriodicValue periodicValue = optional.get();
            if(periodicValue.getDate().isEqual(date)) {
                periodicValue.setQuantity(dto.getQuantity());
                periodicValueRepository.save(periodicValue);
            } else {
                if(periodicValue.getQuantity() != dto.getQuantity()) {
                    setQuantity(ingredient, dto);
                }
            }
        } else {
            setQuantity(ingredient, dto);
        }
    }

}
