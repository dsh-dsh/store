package com.example.store.services;

import com.example.store.mappers.QuantityMapper;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.repositories.QuantityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuantityService {

    @Autowired
    private QuantityRepository quantityRepository;
    @Autowired
    private QuantityMapper quantityMapper;

    public void setQuantities(Ingredient ingredient, List<QuantityDTO> quantityDTOList) {
        quantityDTOList.forEach(dto -> setQuantity(ingredient, dto));
    }

    private void setQuantity(Ingredient ingredient, QuantityDTO dto) {
        Quantity quantity = quantityMapper.mapToItem(dto);
        quantity.setIngredient(ingredient);
        quantityRepository.save(quantity);
    }

}
