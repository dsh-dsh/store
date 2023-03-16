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
    @Autowired
    private IngredientCache ingredientCache;

    private Map<Item, BigDecimal> ingredientMapOfItem;

    public Map<Item, BigDecimal> getIngredientMapOfItem(Item item, BigDecimal quantity, LocalDate date) {
        ingredientMapOfItem = new HashMap<>();
        setIngredientMapOfItemRecursively(item, quantity, date);
        return ingredientMapOfItem;
    }

    private void setIngredientMapOfItemRecursively(Item item, BigDecimal quantity, LocalDate date) {
        // todo refactor this to exclude from getIngredientsNotDeleted() getGrossQuantity and getEnableValue
        // todo due to for each iteration will be at list two more select from db
        List<Ingredient> ingredients = ingredientCache.getValue(item);
        if(ingredients.isEmpty()) {
            ingredientMapOfItem.merge(item, quantity.setScale(3, RoundingMode.HALF_EVEN), BigDecimal::add);
        } else {
            boolean isWeight = isWeight(item);
            float totalWeight = isWeight ? getTotalWeight(ingredients) : 1;
            for(Ingredient ingredient : ingredients) {
                checkForPortionItemInWeightItem(isWeight, ingredient);
                float grossQuantity = periodicValueService.getGrossQuantity(ingredient);
                float enableValue = periodicValueService.getEnableQuantity(ingredient);
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

    public List<Ingredient> getIngredientsNotDeleted(Item item) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        for(Ingredient ingredient : ingredients) {
            float grossQuantity = periodicValueService.getGrossQuantity(ingredient);
            float enableValue = periodicValueService.getEnableQuantity(ingredient);
            if(grossQuantity > 0 && enableValue > 0) return ingredients;
        }
        return new ArrayList<>();
    }

    // todo refactor
    public float getNetQuantity(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getNetQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    public float getTotalWeight(List<Ingredient> ingredients) {
        float netWeight = 0;
        for(Ingredient ingredient : ingredients) {
            if(periodicValueService.getEnableQuantity(ingredient) == 1) {
                netWeight += periodicValueService.getNetQuantity(ingredient);
            }
        }
        if(netWeight == 0) throw new BadRequestException(
                Constants.NO_INGREDIENTS_IN_ITEM_MESSAGE,
                this.getClass().getName() + " - getTotalWeight(List<Ingredient> ingredients, LocalDate date)");
        return netWeight;
    }
}
