package com.example.store.components;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.Unit;
import com.example.store.repositories.IngredientRepository;
import com.example.store.services.ItemService;
import com.example.store.services.PeriodicValueService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Component
public class IngredientCalculation {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private PeriodicValueService periodicValueService;
    @Autowired
    private ItemService itemService;

    @Value("${water.item.number}")
    private int waterItemNumber;

    private Map<Item, BigDecimal> ingredientMapOfItem;

    public Map<Item, BigDecimal> getIngredientMapOfItem(Item item, BigDecimal quantity, LocalDate date) {
        ingredientMapOfItem = new HashMap<>();
        setIngredientMapOfItemRecursively(item, quantity, date);
        return ingredientMapOfItem;
    }

    private void setIngredientMapOfItemRecursively(Item item, BigDecimal quantity, LocalDate date) {
        // todo refactor this to exclude from getIngredientsNotDeleted() getGrossQuantity and getEnableValue
        // todo due to for each iteration will be at list two more select from db
        List<Ingredient> ingredients = getIngredientsNotDeleted(item, date);
        if(ingredients.isEmpty()) {
            if(item.getNumber() != waterItemNumber) { // skip water item
                ingredientMapOfItem.merge(item, quantity.setScale(3, RoundingMode.HALF_EVEN), BigDecimal::add);
            }
        } else {
            boolean isWeight = isWeight(item);
            float totalWeight = isWeight ? getTotalWeight(ingredients, date) : 1;
            for(Ingredient ingredient : ingredients) {
                checkForPortionItemInWeightItem(isWeight, ingredient);
                float grossQuantity = getGrossQuantity(ingredient, date);
                float enableValue = getEnableValue(ingredient, date);
                if(grossQuantity == 0f || enableValue == 0f) continue;
                BigDecimal itemQuantity =
                        BigDecimal.valueOf(grossQuantity / totalWeight)
                        .multiply(quantity);
                setIngredientMapOfItemRecursively(ingredient.getChild(), itemQuantity, date);
            }
        }
    }

    public void checkForPortionItemInWeightItem(boolean isWeight, Ingredient ingredient) {
        if(!isWeight) return;
        Unit itemUnit = ingredient.getChild().getUnit();
        if(itemUnit == Unit.PORTION || itemUnit == Unit.PIECE) {
            throw new HoldDocumentException(
                    String.format(Constants.PORTION_ITEM_MESSAGE,
                            ingredient.getChild().getName(), ingredient.getParent().getName()),
                    this.getClass().getName() + " - deleteOrderDoc(int docId)");
        }
    }

    public boolean isWeight(Item item) {
        Unit unit = item.getUnit();
        return unit == Unit.KG || unit == Unit.LITER;
    }

    public List<Ingredient> getIngredientsNotDeleted(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        for(Ingredient ingredient : ingredients) {
            float grossQuantity = getGrossQuantity(ingredient, date);
            float enableValue = getEnableValue(ingredient, date);
            if(grossQuantity > 0 && enableValue > 0) return ingredients;
        }
        return new ArrayList<>();
    }

    public float getGrossQuantity(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getGrossQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    public float getNetQuantity(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getNetQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    public float getEnableValue(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getEnableQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    public float getTotalWeight(List<Ingredient> ingredients, LocalDate date) {
        float netWeight = 0;
        for(Ingredient ingredient : ingredients) {
            Optional<PeriodicValue> netValue = periodicValueService.getNetQuantity(ingredient, date);
            Optional<PeriodicValue> enableValue = periodicValueService.getEnableQuantity(ingredient, date);
            if(netValue.isPresent() && enableValue.isPresent() && enableValue.get().getQuantity() == 1) {
                netWeight += netValue.get().getQuantity();
            }
        }
        if(netWeight == 0) throw new BadRequestException(
                Constants.NO_INGREDIENTS_IN_ITEM_MESSAGE,
                this.getClass().getName() + " - getTotalWeight(List<Ingredient> ingredients, LocalDate date)");
        return netWeight;
    }
}
