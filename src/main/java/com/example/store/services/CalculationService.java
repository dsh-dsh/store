package com.example.store.services;

import com.example.store.model.dto.CalculationDTO;
import com.example.store.model.dto.IngredientCalculationDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CalculationService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private ItemRestService itemRestService;
    @Autowired
    private PeriodicValueService periodicValueService;

    public CalculationDTO getCalculation(int itenId, long longDate) {
        Item item = itemService.getItemById(itenId);
        LocalDate date = Util.getLocalDate(longDate);
        return getCalculationDTO(item, date);
    }

    public CalculationDTO getCalculationDTO(Item item, LocalDate date) {
        List<Ingredient> ingredients = ingredientService.getIngredientsNotDeleted(item);
        List<IngredientCalculationDTO> list = ingredients.stream()
                .map(ingredient -> getCostCalculation(ingredient, date))
                .collect(Collectors.toList());

        CalculationDTO dto = new CalculationDTO();
        dto.setItemName(item.getName());
        dto.setIngredients(list);
        return dto;
    }

    public IngredientCalculationDTO getCostCalculation(Ingredient ingredient, LocalDate date) {
        Map<Item, Float> map = ingredientService.getIngredientMapOfItem(ingredient.getChild(), date);

        float quantity = 0;
        float amount = 0;
        if(map.size() > 0) {
            for (Map.Entry<Item, Float> entry : map.entrySet()) {
                quantity += entry.getValue();
                float price = itemRestService.getLastPriceOfItem(entry.getKey());
                amount += price * entry.getValue();
            }
        } else {
            quantity = 1;
            amount = itemRestService.getLastPriceOfItem(ingredient.getChild());
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
