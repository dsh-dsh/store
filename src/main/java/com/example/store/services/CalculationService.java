package com.example.store.services;

import com.example.store.components.IngredientCache;
import com.example.store.components.IngredientCalculation;
import com.example.store.components.PeriodicValuesCache;
import com.example.store.model.dto.CalculationDTO;
import com.example.store.model.dto.IngredientCalculationDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalculationService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientCalculation ingredientCalculation;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private PeriodicValueService periodicValueService;
    @Autowired
    private IngredientCache ingredientCache;
    @Autowired
    PeriodicValuesCache periodicValuesCache;

    public CalculationDTO getCalculation(int itemId, long longDate) {
        Item item = itemService.findItemById(itemId);
        LocalDate date = Util.getLocalDate(longDate);
        return getCalculationDTO(item, date);
    }

    public CalculationDTO getCalculationDTO(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        List<IngredientCalculationDTO> list = ingredients.stream()
                .map(ingredient -> getCostCalculation(ingredient, date))
                .collect(Collectors.toList());
        CalculationDTO dto = new CalculationDTO();
        dto.setItemName(item.getName());
        dto.setIngredients(list);
        return dto;
    }

    public IngredientCalculationDTO getCostCalculation(Ingredient ingredient, LocalDate date) {
        BigDecimal itemQuantity = BigDecimal.valueOf(ingredientCalculation.getNetQuantity(ingredient, date));
        ingredientCache.resetCache();
        periodicValuesCache.setPeriodicQuantities();
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(ingredient.getChild(), itemQuantity, date);
        float quantity = 0;
        float amount = 0;
        if(map.size() > 0) {
            for (Map.Entry<Item, BigDecimal> entry : map.entrySet()) {
                quantity += entry.getValue().floatValue();
                float price = itemRestService.getLastPriceOfItem(entry.getKey(), date.atStartOfDay());
                amount += price * entry.getValue().floatValue();
            }
        } else {
            quantity = 1;
            amount = itemRestService.getLastPriceOfItem(ingredient.getChild(), date.atStartOfDay());
        }

        float net = 0;
        float gross = 0;
        List<PeriodicValue> values = periodicValueService.getQuantityList(ingredient, date);
        for(PeriodicValue value : values) {
            if(value.getType() == PeriodicValueType.NET) net = value.getQuantity();
            if(value.getType() == PeriodicValueType.GROSS) gross = value.getQuantity();
        }

        IngredientCalculationDTO dto = new IngredientCalculationDTO();
        dto.setItemName(ingredient.getChild().getName());
        if(quantity != 0) {
            dto.setCostPrice(amount / quantity);
            dto.setNet(net);
            dto.setGross(gross);
            dto.setAmount((amount / quantity) * net);
        }

        return dto;
    }
}
