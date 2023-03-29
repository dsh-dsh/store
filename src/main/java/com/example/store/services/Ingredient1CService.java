package com.example.store.services;

import com.example.store.model.dto.Ingredient1CDTO;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Ingredient1CService extends IngredientService {

    @Autowired
    private PeriodicValue1CService periodicValue1CService;
    @Autowired
    private ItemService itemService;

    //todo add tests
    public List<Ingredient1CDTO> getIngredient1CDTOList(Item item, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        return ingredients.stream()
                .map(ingredient -> {
                    Ingredient1CDTO dto = ingredientMapper.mapTo1CDTO(ingredient);
                    setPeriodicValueFields(dto, periodicValueService.getPeriodicValueDTOList(ingredient, date));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void updateIngredients(Item item, List<IngredientDTO> ingredientDTOList) {
        if(ingredientDTOList == null) return;
        Map<Integer, Ingredient> ingredientMap = getIdIngredientMap(item);
        for(IngredientDTO ingredientDTO : ingredientDTOList) {
            Optional<Item> optionalItem = itemService.findItemByNumber(ingredientDTO.getChildId());
            if(optionalItem.isPresent()) {
                int childId = optionalItem.get().getId();
                if (ingredientMap.containsKey(childId)) {
                    Ingredient ingredient = ingredientMap.get(childId);
                    updateIngredient(ingredient, ingredientDTO);
                    ingredientMap.remove(childId);
                } else {
                    setIngredient(item, ingredientDTO);
                }
            }
        }
    }

    @Override
    public void setIngredient(Item item, IngredientDTO dto) {
        Ingredient ingredient = mapToEntity(item, dto);
        ingredientRepository.save(ingredient);
        periodicValue1CService.setQuantities(ingredient, dto);
    }

    @Override
    public void updateIngredient(Ingredient ingredient, IngredientDTO dto) {
        ingredient.setDeleted(dto.isDeleted());
        ingredientRepository.save(ingredient);
        periodicValue1CService.updateQuantities(ingredient, dto);
    }

    public Ingredient mapToEntity(Item item, IngredientDTO dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setDeleted(dto.isDeleted());
        ingredient.setParent(item);
        ingredient.setChild(itemService.getItemByNumber(dto.getChildId()));
        return ingredient;
    }
}
