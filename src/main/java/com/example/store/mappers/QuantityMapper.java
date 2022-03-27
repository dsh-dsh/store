package com.example.store.mappers;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class QuantityMapper {

    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.createTypeMap(Quantity.class, QuantityDTO.class);
        modelMapper.createTypeMap(QuantityDTO.class, Quantity.class);
    }

    public QuantityDTO mapToDTO(Quantity quantity) {
        return modelMapper.map(quantity, QuantityDTO.class);
    }

    public Quantity mapToItem(QuantityDTO dto) {
        return modelMapper.map(dto, Quantity.class);
    }
}