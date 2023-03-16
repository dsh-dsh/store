package com.example.store.services;

import com.example.store.components.IngredientCalculation;
import com.example.store.components.PeriodicValuesCache;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.IngredientMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.IngredientRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IngredientService {

    @Autowired
    protected IngredientMapper ingredientMapper;
    @Autowired
    protected IngredientRepository ingredientRepository;
    @Autowired
    private PeriodicValueService periodicValueService;
    @Autowired
    private IngredientCalculation ingredientCalculation;
    @Autowired
    PeriodicValuesCache periodicValuesCache;

    public Ingredient getIngredientById(int id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_ITEM_MESSAGE,
                        this.getClass().getName() + " - getIngredientById(int id)"));
    }

    public Map<Item, BigDecimal> getIngredientQuantityMap(Map<Item, BigDecimal> itemMap, LocalDate date) {
        periodicValuesCache.setPeriodicQuantities();
        return itemMap.entrySet().stream()
                .filter(entry -> haveIngredients(entry.getKey()))
                .flatMap(itemEntry -> ingredientCalculation.getIngredientMapOfItem(itemEntry.getKey(), itemEntry.getValue(), date)
                        .entrySet().stream()
                        .map(item -> new AbstractMap.SimpleImmutableEntry<>(
                                item.getKey(), item.getValue().setScale(3, RoundingMode.HALF_EVEN))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, BigDecimal::add));
    }

    public Map<Integer, Ingredient> getIdIngredientMap(Item item) {
        return getIngredientsNotDeleted(item).stream()
                .collect(Collectors.toMap(
                        ingredient -> ingredient.getChild().getId(),
                        Function.identity()));
    }

    public void updateIngredients(Item item, List<IngredientDTO> ingredientDTOList) {
        if (ingredientDTOList == null) return;
        Map<Integer, Ingredient> ingredientMap = getIdIngredientMap(item);
        for (IngredientDTO ingredientDTO : ingredientDTOList) {
            int childId = ingredientDTO.getChildId();
            if (ingredientMap.containsKey(childId)) {
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
        periodicValueService.updateQuantities(ingredient, dto);
    }

    public List<IngredientDTO> getIngredientDTOList(Item item, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        return ingredients.stream()
                .map(ingredient -> {
                    IngredientDTO dto = ingredientMapper.mapToDTO(ingredient);
                    setPeriodicValueFields(dto, periodicValueService.getPeriodicValueDTOList(ingredient, date));
                    return dto;
                }).collect(Collectors.toList());
    }

    protected void setPeriodicValueFields(IngredientDTO dto, List<PeriodicValueDTO> valueDTOList) {
        for (PeriodicValueDTO valueDTO : valueDTOList) {
            switch (valueDTO.getType()) {
                case "NET":
                    dto.setNetto(valueDTO);
                    break;
                case "GROSS":
                    dto.setGross(valueDTO);
                    break;
                case "ENABLE":
                    dto.setEnable(valueDTO);
                    break;
                default:
            }
        }
    }

    public List<Ingredient> getIngredientsNotDeleted(Item item) {
        return ingredientRepository.findByParentAndIsDeleted(item, false);
    }

    public void setIngredients(Item item, List<IngredientDTO> ingredientDTOS) {
        if (ingredientDTOS == null) return;
        ingredientDTOS.forEach(dto -> setIngredient(item, dto));
    }

    public void setIngredient(Item item, IngredientDTO dto) {
        Ingredient ingredient = ingredientMapper.mapToEntity(dto);
        ingredient.setParent(item);
        ingredientRepository.save(ingredient);
        periodicValueService.setQuantities(ingredient, dto);
    }

    public void softDeleteIngredients(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        ingredients.forEach(ingredient -> softDeleteIngredient(ingredient, date));
    }

    protected void softDeleteIngredient(Ingredient ingredient, LocalDate date) {
        ingredient.setDeleted(true);
        ingredientRepository.save(ingredient);
        periodicValueService.softDeleteQuantities(ingredient, date);
    }

    public boolean haveIngredients(Item item) {
        return ingredientRepository.existsByParentAndIsDeleted(item, false);
    }

    public void addInnerItems(List<DocumentItem> docItems, LocalDate date) {
        ItemDoc document = docItems.get(0).getItemDoc();
        List<DocumentItem> itemsWithIngredients = docItems.stream()
                .filter(docItem -> haveIngredients(docItem.getItem()))
                .collect(Collectors.toList());
        Map<Item, BigDecimal> docItemMap = itemsWithIngredients.stream()
                .collect(Collectors.toMap(
                        DocumentItem::getItem,
                        DocumentItem::getQuantity,
                        BigDecimal::add));
        Map<Item, BigDecimal> itemMap = getIngredientQuantityMap(docItemMap, date);
        List<DocumentItem> listOfIngredients = itemMap.entrySet().stream()
                .map(set -> new DocumentItem(document, set.getKey(), set.getValue()))
                .collect(Collectors.toList());
        docItems.removeAll(itemsWithIngredients);
        docItems.addAll(listOfIngredients);
    }

}
