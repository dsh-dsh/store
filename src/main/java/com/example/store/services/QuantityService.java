package com.example.store.services;

import com.example.store.mappers.QuantityMapper;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.repositories.QuantityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuantityService {

    @Autowired
    private QuantityRepository quantityRepository;
    @Autowired
    private QuantityMapper quantityMapper;

    public List<Quantity> getQuantityList(Ingredient ingredient, LocalDate date) {
        return quantityRepository.findByIngredientAndDateLessThanEqual(ingredient, date);
    }

    public List<QuantityDTO> getQuantityDTOList(List<Quantity> quantities) {
        return quantities.stream().map(quantityMapper::mapToDTO).collect(Collectors.toList());
    }

    public List<QuantityDTO> getQuantityDTOList(Ingredient ingredient, LocalDate date) {
        List<Quantity> quantities = getQuantityList(ingredient, date);
        return quantities.stream().map(quantityMapper::mapToDTO).collect(Collectors.toList());
    }

    public void setQuantities(Ingredient ingredient, List<QuantityDTO> quantityDTOList) {
        quantityDTOList.forEach(dto -> setQuantity(ingredient, dto));
    }

    private void setQuantity(Ingredient ingredient, QuantityDTO dto) {
        Quantity quantity = quantityMapper.mapToItem(dto);
        quantity.setIngredient(ingredient);
        quantityRepository.save(quantity);
    }

}
