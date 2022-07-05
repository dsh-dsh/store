package com.example.store.services;

import com.example.store.mappers.PeriodicValueMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.repositories.PeriodicValueRepository;
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
public class PeriodicValueService {

    @Autowired
    private PeriodicValueRepository periodicValueRepository;
    @Autowired
    private PeriodicValueMapper periodicValueMapper;

    public List<PeriodicValue> getQuantityList(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> list = new ArrayList<>();
        for(PeriodicValueType type : PeriodicValueType.values()) {
            Optional<PeriodicValue> optionalQuantity = periodicValueRepository
                    .findFirstByIngredientAndDateLessThanEqualAndType(ingredient, date, type, Sort.by("date").descending());
            optionalQuantity.ifPresent(list::add);
        }
        return list;
    }

    public List<PeriodicValueDTO> getQuantityDTOList(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> quantities = getQuantityList(ingredient, date);
        return quantities.stream().map(periodicValueMapper::mapToDTO).collect(Collectors.toList());
    }

    public float getQuantityRatio(Ingredient ingredient, LocalDate date) {
        List<PeriodicValue> quantities = getQuantityList(ingredient, date);
        Optional<PeriodicValue> netQuantity = quantities.stream().
                filter(q -> q.getType().equals(PeriodicValueType.NET)).findFirst();
        Optional<PeriodicValue> grossQuantity = quantities.stream().
                filter(q -> q.getType().equals(PeriodicValueType.GROSS)).findFirst();
        if(netQuantity.isEmpty() || grossQuantity.isEmpty()) return 0f;
        return netQuantity.get().getQuantity() / grossQuantity.get().getQuantity();
    }

    public Optional<PeriodicValue> getGrossQuantity(Ingredient ingredient, LocalDate date) {
        return getQuantityList(ingredient, date).stream().
                filter(q -> q.getType().equals(PeriodicValueType.GROSS))
                .findFirst();
    }

    public void setQuantities(Ingredient ingredient, IngredientDTO dto) {
        setQuantity(ingredient, dto.getNetto());
        setQuantity(ingredient, dto.getGross());
        setQuantity(ingredient, dto.getEnable());
    }

    private void setQuantity(Ingredient ingredient, PeriodicValueDTO dto) {
        PeriodicValue periodicValue = periodicValueMapper.mapToItem(dto);
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

    private void updateQuantity(Ingredient ingredient, PeriodicValueDTO dto) {
        PeriodicValueType type = PeriodicValueType.valueOf(dto.getType());
        LocalDate date = convertDate(dto.getDate());
        Optional<PeriodicValue> optional = periodicValueRepository.findById(dto.getId());
//                = quantityRepository.findTop1ByTypeAndDateLessThanEqualOrderByDateDesc(type, date);
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

    private LocalDate convertDate(long longTime) {
        return Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}
