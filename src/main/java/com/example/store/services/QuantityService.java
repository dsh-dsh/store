package com.example.store.services;

import com.example.store.mappers.QuantityMapper;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import com.example.store.repositories.QuantityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        return quantityRepository
                .findByIngredientAndDateLessThanEqual(ingredient, date, Sort.by("date").descending());
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

    public void updateQuantities(Ingredient ingredient, List<QuantityDTO> dtoList) {
        dtoList.forEach(dto -> updateQuantity(ingredient, dto));
    }

    private void updateQuantity(Ingredient ingredient, QuantityDTO dto) {
        QuantityType type = QuantityType.valueOf(dto.getType());
        LocalDate date = LocalDate.parse(dto.getDate());
        Optional<Quantity> optional
                = quantityRepository.findTop1ByTypeAndDateLessThanEqualOrderByDateDesc(type, date);
        if(optional.isPresent()) {
            Quantity quantity = optional.get();
            if(quantity.getDate().isEqual(LocalDate.parse(dto.getDate()))) {
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
}
