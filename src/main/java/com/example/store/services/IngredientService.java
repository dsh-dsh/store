package com.example.store.services;

import com.example.store.mappers.IngredientMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    @Autowired
    private IngredientMapper ingredientMapper;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private QuantityService quantityService;

    // TODO написать Unit тесты к этому методу
    public void updateIngredients(Item item, List<IngredientDTO> ingredientDTOList) {
        if(ingredientDTOList == null) return;
        Map<Integer, Ingredient> ingredientMap = getIngredientMap(item);

        for(IngredientDTO ingredientDTO : ingredientDTOList) {
            if(ingredientMap.containsKey(ingredientDTO.getChild().getId())) {
                Ingredient ingredient = ingredientMap.get(ingredientDTO.getChild().getId());
                updateIngredient(ingredient, ingredientDTO);
                ingredientMap.remove(ingredientDTO.getChild().getId());
            } else {
                setIngredient(item, ingredientDTO);
            }
        }
        ingredientMap.forEach((key, value) -> softDeleteIngredient(value));
    }

    private void softDeleteIngredient(Ingredient ingredient) {
        ingredient.setDeleted(true);
        ingredientRepository.save(ingredient);
    }

    private void updateIngredient(Ingredient ingredient, IngredientDTO dto) {
        ingredient.setDeleted(dto.isDeleted());
        ingredientRepository.save(ingredient);
        quantityService.updateQuantities(ingredient, dto.getQuantityList());
    }

    private Map<Integer, Ingredient> getIngredientMap(Item item) {
        return  getIngredientsNotDeleted(item).stream()
                .collect(Collectors.toMap(
                        ingredient -> ingredient.getChild().getId(),
                        Function.identity()));
    }

    public void setIngredients(Item item, List<IngredientDTO> ingredientDTOS) {
        if(ingredientDTOS == null) return;
        ingredientDTOS.forEach(dto -> setIngredient(item, dto));
    }

    public List<IngredientDTO> getIngredientDTOList(Item item, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        List<IngredientDTO> dtoList = ingredients.stream()
                .map(ingredient -> {
                    IngredientDTO dto = ingredientMapper.mapToDTO(ingredient);
                    dto.setQuantityList(quantityService.getQuantityDTOList(ingredient, date));
                    return dto;
                }).collect(Collectors.toList());
        return dtoList;
    }

    public List<Ingredient> getIngredientsNotDeleted(Item item) {
        return ingredientRepository.findByParentAndIsDeleted(item, false);
    }

    private void setIngredient(Item item, IngredientDTO dto) {
        Ingredient ingredient = ingredientMapper.mapToEntity(dto);
        ingredient.setParent(item);
        ingredientRepository.save(ingredient);
        quantityService.setQuantities(ingredient, dto.getQuantityList());
    }

    public void softDeleteIngredients(Item item) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        ingredients.forEach(this::softDelete);
    }

    private void softDelete(Ingredient ingredient) {
        ingredient.setDeleted(true);
        ingredientRepository.save(ingredient);
    }

}
