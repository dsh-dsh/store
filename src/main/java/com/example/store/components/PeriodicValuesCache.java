package com.example.store.components;

import com.example.store.model.enums.PeriodicValueType;
import com.example.store.model.projections.IngredientQuantityProjection;
import com.example.store.repositories.PeriodicValueRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class PeriodicValuesCache {

    @Autowired
    PeriodicValueRepository periodicValueRepository;

    private LocalDateTime cacheCreation;
    private Map<Integer, Float> netQuantities;
    private Map<Integer, Float> grossQuantities;
    private Map<Integer, Float> enableValues;

    // todo add tests
    public void setPeriodicQuantities() {
        if(cacheCreation == null || cacheCreation.isBefore(LocalDate.now().atStartOfDay())) {
            LocalDateTime dateTime = LocalDateTime.now();
            setValues(dateTime);
            cacheCreation = LocalDateTime.now();
        }
    }
    public void setPeriodicQuantitiesOnDate(LocalDate date) {
        setValues();
        cacheCreation = date.atStartOfDay();
    }

    public void setValues() {
        LocalDateTime dateTime = LocalDateTime.now();
        netQuantities = getPeriodicValues(PeriodicValueType.NET, dateTime);
        grossQuantities = getPeriodicValues(PeriodicValueType.GROSS, dateTime);
        enableValues = getPeriodicValues(PeriodicValueType.ENABLE, dateTime);
    }

    public void setValues(LocalDateTime dateTime) {
        netQuantities = getPeriodicValues(PeriodicValueType.NET, dateTime);
        grossQuantities = getPeriodicValues(PeriodicValueType.GROSS, dateTime);
        enableValues = getPeriodicValues(PeriodicValueType.ENABLE, dateTime);
        cacheCreation = dateTime;
    }

    // todo add tests
    protected Map<Integer, Float> getPeriodicValues(PeriodicValueType type, LocalDateTime dateTime) {
        List<IngredientQuantityProjection> list = periodicValueRepository.getPeriodicQuantitiesOfType(type.toString(), dateTime);
        Map<Integer, Float> map = new HashMap<>();
        int id = 0;
        for (int i = 0; i < list.size(); i++) {
            if(id != list.get(i).getId()) {
                IngredientQuantityProjection projection = list.get(i);
                map.put(projection.getId(), projection.getQuantity());
                id = projection.getId();
            }
        }
        return map;
    }

    // todo add tests
    public void clearPeriodicQuantities() {
        netQuantities = null;
        grossQuantities = null;
        enableValues = null;
        cacheCreation = null;
    }
}
