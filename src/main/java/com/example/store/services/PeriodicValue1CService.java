package com.example.store.services;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.utils.Util;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PeriodicValue1CService extends PeriodicValueService {

    @Override
    public void setQuantities(Ingredient ingredient, IngredientDTO dto) {
        dto.getQuantityList().forEach(quantityDTO -> setQuantity(ingredient, quantityDTO));
    }

    @Override
    public void updateQuantities(Ingredient ingredient, IngredientDTO dto) {
        dto.getQuantityList().forEach(quantityDTO -> updateQuantity(ingredient, quantityDTO));
    }

    @Override
    protected void updateQuantity(Ingredient ingredient, PeriodicValueDTO dto) {
        PeriodicValueType type = PeriodicValueType.valueOf(dto.getType());
        LocalDate date = Util.getLocalDate(dto.getDate());
        Optional<PeriodicValue> optional = periodicValueRepository
                .findFirstByIngredientAndDateAndType(ingredient, date, type);
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
