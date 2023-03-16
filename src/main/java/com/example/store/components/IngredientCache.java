package com.example.store.components;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.repositories.IngredientRepository;
import com.example.store.services.PeriodicValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IngredientCache {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    PeriodicValueService periodicValueService;

    private Map<Item, List<Ingredient>> cache = new HashMap<>();
    LocalDate creationDate;

    // todo add test
    public List<Ingredient> getValue(Item item) {
        LocalDate date = LocalDate.now();
        if(creationDate == null) creationDate = date;
        if(creationDate.getDayOfMonth() < date.getDayOfMonth()) {
            creationDate = date;
            resetCache();
        }
        return cache.computeIfAbsent(item, i -> getIngredientsNotDeleted(item));
    }

    public void resetCache() {
        cache.clear();
    }

    protected List<Ingredient> getIngredientsNotDeleted(Item item) {
        List<Ingredient> ingredients = ingredientRepository.findByParentAndIsDeleted(item, false);
        for(Ingredient ingredient : ingredients) {
            float grossQuantity = periodicValueService.getGrossQuantity(ingredient);
            float enableValue = periodicValueService.getEnableQuantity(ingredient);
            if(grossQuantity > 0 && enableValue > 0) return ingredients;
        }
        return new ArrayList<>();
    }
}
