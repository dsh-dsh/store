package com.example.store.services;

import com.example.store.mappers.IngredientMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Quantity;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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

    private Map<Item, Float> ingredientMapOfItem;

    public boolean haveIngredients(Item item) {
        return ingredientRepository.existsByParentAndIsDeleted(item, false);
    }

    public Map<Item, Float> getIngredientMap(Map<Item, Float> itemMap, LocalDate date) {
        return itemMap.entrySet().stream()
                .flatMap(itemEntry -> getIngredientMapOfItem(itemEntry.getKey(), date)
                            .entrySet().stream()
                            .map(item -> new AbstractMap.SimpleImmutableEntry<>(
                                    item.getKey(), item.getValue() * itemEntry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Float::sum));
    }

    public Map<Item, Float> getIngredientMapOfItem(Item item, LocalDate date) {
        ingredientMapOfItem = new HashMap<>();
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        ingredients.forEach(ingredient ->
                setIngredientMapOfItemRecursively(ingredient,
                        quantityService.getQuantityRatio(ingredient, date), date));
        return ingredientMapOfItem;
    }

    private void setIngredientMapOfItemRecursively(Ingredient currentIngredient, float quantityRatio, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(currentIngredient.getChild());
        if(!ingredients.isEmpty()) {
            for(Ingredient ingredient : ingredients) {
                float ratio = quantityService.getQuantityRatio(ingredient, date) * quantityRatio;
                setIngredientMapOfItemRecursively(ingredient, ratio, date);
            }
        } else {
            Optional<Quantity> grossQuantity = quantityService.getGrossQuantity(currentIngredient, date);
            if(grossQuantity.isEmpty()) return;
            ingredientMapOfItem.put(currentIngredient.getChild(), grossQuantity.get().getQuantity() * quantityRatio);
        }
    }

    // todo refactor this
    public void updateIngredients(Item item, List<IngredientDTO> ingredientDTOList) {
        if(ingredientDTOList == null) return;
        Map<Integer, Ingredient> ingredientMap = getIdIngredientMap(item);
        for(IngredientDTO ingredientDTO : ingredientDTOList) {
            int childId = ingredientDTO.getChildId();
            if(ingredientMap.containsKey(childId)) {
                Ingredient ingredient = ingredientMap.get(childId);
                updateIngredient(ingredient, ingredientDTO);
                ingredientMap.remove(childId);
            } else {
                setIngredient(item, ingredientDTO);
            }
        }
    }

    public void updateIngredient(Ingredient ingredient, IngredientDTO dto) {
        ingredient.setDeleted(dto.isDeleted());
        ingredientRepository.save(ingredient);
        quantityService.updateQuantities(ingredient, dto);
    }

    public Map<Integer, Ingredient> getIdIngredientMap(Item item) {
        return  getIngredientsNotDeleted(item).stream()
                .collect(Collectors.toMap(
                        ingredient -> ingredient.getChild().getId(),
                        Function.identity()));
    }

    public List<IngredientDTO> getIngredientDTOList(Item item, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        return ingredients.stream()
                .map(ingredient -> {
                    IngredientDTO dto = ingredientMapper.mapToDTO(ingredient);

                    // todo refactor this
                    dto.setQuantityList(quantityService.getQuantityDTOList(ingredient, date));
                    dto.setName(dto.getChild().getName());
                    dto.setChildId(dto.getChild().getId());
                    dto.setParentId(dto.getParent().getId());
                    for(QuantityDTO quantityDTO : dto.getQuantityList()) {
                        if(quantityDTO.getType().equals("NET")) {
                            dto.setNetto(quantityDTO);
                        } else if(quantityDTO.getType().equals("GROSS")) {
                            dto.setGross(quantityDTO);
                        } else if(quantityDTO.getType().equals("ENABLE")){
                            dto.setEnable(quantityDTO);
                        }
                    }
                    // todo until here

                    return dto;
                }).collect(Collectors.toList());
    }



    public List<IngredientDTO> getDeletedIngredientDTOList(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, true);
        return ingredients.stream()
                .map(ingredient -> {
                    IngredientDTO dto = ingredientMapper.mapToDTO(ingredient);
                    dto.setQuantityList(quantityService.getQuantityDTOList(ingredient, date));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<Ingredient> getIngredientsNotDeleted(Item item) {
        return ingredientRepository.findByParentAndIsDeleted(item, false);
    }

    public void setIngredients(Item item, List<IngredientDTO> ingredientDTOS) {
        if(ingredientDTOS == null) return;
        ingredientDTOS.forEach(dto -> setIngredient(item, dto));
    }

    public void setIngredient(Item item, IngredientDTO dto) {
        Ingredient ingredient = ingredientMapper.mapToEntity(dto);
        ingredient.setParent(item);
        ingredientRepository.save(ingredient);
        quantityService.setQuantities(ingredient, dto);
    }

    public void softDeleteIngredients(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        ingredients.forEach(ingredient -> softDeleteIngredient(ingredient, date));
    }

    private void softDeleteIngredient(Ingredient ingredient, LocalDate date) {
        ingredient.setDeleted(true);
        ingredientRepository.save(ingredient);
        quantityService.softDeleteQuantities(ingredient, date);
    }

    public void addInnerItems(List<DocumentItem> docItems, LocalDate date) {
        ItemDoc document = docItems.get(0).getItemDoc();
        List<DocumentItem> itemsWithIngredients = docItems.stream()
                .filter(docItem -> haveIngredients(docItem.getItem()))
                .collect(Collectors.toList());
        Map<Item, Float> docItemMap = itemsWithIngredients.stream()
                .collect(Collectors.toMap(
                        DocumentItem::getItem,
                        DocumentItem::getQuantity,
                        Float::sum));
        Map<Item, Float> itemMap = getIngredientMap(docItemMap, date);
        List<DocumentItem> listOfIngredients = itemMap.entrySet().stream()
                .map(set -> new DocumentItem(document, set.getKey(), set.getValue()))
                .collect(Collectors.toList());
        docItems.removeAll(itemsWithIngredients);
        docItems.addAll(listOfIngredients);
    }

}
