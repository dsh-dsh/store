package com.example.store.mappers;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.entities.Ingredient;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class IngredientMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    @PostConstruct
    private void init() {
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        modelMapper.createTypeMap(Ingredient.class, IngredientDTO.class)
                .addMappings(mapper -> mapper.using(itemNameConverter).map(Ingredient::getChild, IngredientDTO::setName))
                .addMappings(mapper -> mapper.using(itemIdConverter).map(Ingredient::getParent, IngredientDTO::setParentId))
                .addMappings(mapper -> mapper.using(itemIdConverter).map(Ingredient::getChild, IngredientDTO::setChildId))
                .addMappings(mapper -> mapper.skip(Ingredient::getPeriodicValueList, IngredientDTO::setQuantityList));
        modelMapper.createTypeMap(IngredientDTO.class, Ingredient.class)
                .addMappings(mapper -> mapper.using(idToItemConverter).map(IngredientDTO::getChildId, Ingredient::setChild))
                .addMappings(mapper -> mapper.using(idToItemConverter).map(IngredientDTO::getParentId, Ingredient::setParent))
                .addMappings(mapper -> mapper.skip(IngredientDTO::getQuantityList, Ingredient::setPeriodicValueList));
    }

    public IngredientDTO mapToDTO(Ingredient ingredient) {
        return modelMapper.map(ingredient, IngredientDTO.class);
    }

    public Ingredient mapToEntity(IngredientDTO ingredientDTO) {
        return modelMapper.map(ingredientDTO, Ingredient.class);
    }

}
