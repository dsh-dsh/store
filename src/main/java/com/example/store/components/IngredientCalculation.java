package com.example.store.components;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.Unit;
import com.example.store.repositories.IngredientRepository;
import com.example.store.services.PeriodicValueService;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class IngredientCalculation {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private PeriodicValueService periodicValueService;

    private Map<Item, Float> ingredientMapOfItem;

    public Map<Item, Float> getIngredientMapOfItem(Item item, float quantity, LocalDate date) {
        ingredientMapOfItem = new HashMap<>();
        setIngredientMapOfItemRecursively(item, quantity, date);
        return ingredientMapOfItem;
    }

    private void setIngredientMapOfItemRecursively(Item item, float quantity, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        if(ingredients.isEmpty()) {
            ingredientMapOfItem.put(item, Util.floorValue(quantity, 3));
        } else {
            boolean isWeight = isWeight(item);
            float totalWeight = isWeight ? getTotalWeight(ingredients, date) : 1;
            for(Ingredient ingredient : ingredients) {
                checkForPortionItemInWeightItem(isWeight, item, ingredient.getChild());
                float itemQuantity = getGrossQuantity(ingredient, date) / totalWeight * quantity;
                setIngredientMapOfItemRecursively(ingredient.getChild(), itemQuantity, date);
            }
        }
    }

    // todo add tests
    protected void checkForPortionItemInWeightItem(boolean isWeight, Item parent, Item child) {
        if(!isWeight) return;
        Unit itemUnit = child.getUnit();
        if(itemUnit == Unit.PORTION || itemUnit == Unit.PIECE) {
            throw new HoldDocumentException(
                    String.format(Constants.PORTION_ITEM_MESSAGE, child.getName(), parent.getName()),
                    this.getClass().getName() + " - deleteOrderDoc(int docId)");
        }
    }

    // todo add tests
    protected boolean isWeight(Item item) {
        Unit unit = item.getUnit();
        return unit == Unit.KG || unit == Unit.LITER;
    }

    public List<Ingredient> getIngredientsNotDeleted(Item item) {
        return ingredientRepository.findByParentAndIsDeleted(item, false);
    }

    public float getGrossQuantity(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getGrossQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    public float getNetQuantity(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getNetQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    // todo add tests
    private float getTotalWeight(List<Ingredient> ingredients, LocalDate date) {
        float netWeight = 0;
        for(Ingredient ingredient : ingredients) {
            // todo чтобы правильно списывать ингредиенты из порционной позиции, которая входит в весовое блюдо
            // todo нужно видимо исключить его вес из общего веса этого весового блюда
            Optional<PeriodicValue> netValue = periodicValueService.getNetQuantity(ingredient, date);
            if(netValue.isPresent()) {
                netWeight += netValue.get().getQuantity();
            }
        }
        // todo если общий вес 0, то все равно, так как ингредиенты не правильно заполнены
        // todo может тогда при 0 делать исключение в котором сообщать, что в item не заполнены ингредиенты
        return netWeight == 0 ? 1 : netWeight;
    }
}
