package com.example.store.components;

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

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    @Autowired
    private ItemService itemService;

    @Value("${water.item.number}")
    private int waterItemNumber;

    private Map<Item, BigDecimal> ingredientMapOfItem;
    private Item waterItem;

    @PostConstruct
    public void init() {
        waterItem = itemService.getItemByNumber(waterItemNumber);
    }

    public Map<Item, BigDecimal> getIngredientMapOfItem(Item item, BigDecimal quantity, LocalDate date) {
        ingredientMapOfItem = new HashMap<>();
        setIngredientMapOfItemRecursively(item, quantity, date);
        return ingredientMapOfItem;
    }

    // todo update tests because of merge(), getTotalWeight and enableValue
    private void setIngredientMapOfItemRecursively(Item item, BigDecimal quantity, LocalDate date) {
        List<Ingredient> ingredients = getIngredientsNotDeleted(item);
        if(ingredients.isEmpty()) {
            if(!item.equals(waterItem)) { // skip water item
                ingredientMapOfItem.merge(item, quantity, BigDecimal::add);
            }
        } else {
            boolean isWeight = isWeight(item);
            float totalWeight = isWeight ? getTotalWeight(ingredients, date) : 1;
            for(Ingredient ingredient : ingredients) {
                checkForPortionItemInWeightItem(isWeight, item, ingredient.getChild());
                float grossQuantity = getGrossQuantity(ingredient, date);
                float enableValue = getEnableValue(ingredient, date);
                if(grossQuantity == 0f || enableValue == 0f) continue;
                BigDecimal itemQuantity =
                        BigDecimal.valueOf(grossQuantity / totalWeight).setScale(3, RoundingMode.HALF_EVEN)
                        .multiply(quantity);
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
    public float getEnableValue(Ingredient ingredient, LocalDate date) {
        Optional<PeriodicValue> optional = periodicValueService.getEnableQuantity(ingredient, date);
        return optional.map(PeriodicValue::getQuantity).orElse(0f);
    }

    // todo add tests
    private float getTotalWeight(List<Ingredient> ingredients, LocalDate date) {
        float netWeight = 0;
        for(Ingredient ingredient : ingredients) {
            Optional<PeriodicValue> netValue = periodicValueService.getNetQuantity(ingredient, date);
            Optional<PeriodicValue> enableValue = periodicValueService.getEnableQuantity(ingredient, date);
            if(netValue.isPresent() && enableValue.isPresent() && enableValue.get().getQuantity() == 1) {
                netWeight += netValue.get().getQuantity();
            }
        }
        // todo если общий вес 0, то все равно, так как ингредиенты не правильно заполнены
        // todo может тогда при 0 делать исключение в котором сообщать, что в item не заполнены ингредиенты
        return netWeight == 0 ? 1 : netWeight;
    }
}
