package com.example.store.services;

import com.example.store.components.IngredientCalculation;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Item;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class IngredientCalculationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientCalculation ingredientCalculation;

    @Sql(value = "/sql/ingredients/smashPotatoIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemSmashPotatoTest() {
        Item item = itemService.getItemById(10);
        Item potato = itemService.getItemById(14);
        Item milk = itemService.getItemById(15);
        Item butter = itemService.getItemById(16);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(0.196F, map.get(potato).floatValue());
        assertEquals(0.047F, map.get(milk).floatValue());
        assertEquals(0.015F, map.get(butter).floatValue());
    }

    @Sql(value = "/sql/ingredients/threeStepsIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemThreeStepsTest() {
        Item item = itemService.getItemById(10);
        Item ingr1 = itemService.getItemById(14);
        Item ingr2 = itemService.getItemById(15);
        Item ingr3 = itemService.getItemById(16);
        Item ingr4 = itemService.getItemById(17);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(1.5f, map.get(ingr1).floatValue());
        assertEquals(1.8f, map.get(ingr2).floatValue());
        assertEquals(0.6f, map.get(ingr3).floatValue());
        assertEquals(0.6f, map.get(ingr4).floatValue());
    }

    @Sql(value = "/sql/ingredients/draftBeer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemBearTest() {
        Item bear = itemService.getItemById(10);
        Item bearIngredient = itemService.getItemById(11);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(bear, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(0.3F, map.get(bearIngredient).floatValue());
    }

    @Sql(value = "/sql/ingredients/bottledBeer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemBeverageTest() {
        Item bear = itemService.getItemById(10);
        Item bearIngredient = itemService.getItemById(11);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(bear, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(1F, map.get(bearIngredient).floatValue());
    }

    @Sql(value = "/sql/ingredients/smallValues.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemSmallValuesTest() {
        Item item = itemService.getItemById(10);
        Item ingr1 = itemService.getItemById(14);
        Item ingr2 = itemService.getItemById(15);
        Item ingr3 = itemService.getItemById(16);
        Item ingr4 = itemService.getItemById(17);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(0.186F, map.get(ingr1).floatValue());
        assertEquals(0.32F, map.get(ingr2).floatValue());
        assertEquals(0.45F, map.get(ingr3).floatValue());
        assertEquals(0.54F, map.get(ingr4).floatValue());
    }

    @Sql(value = "/sql/ingredients/smallValues.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemItemIsWeightTest() {
        Item item = itemService.getItemById(11);
        Item ingr3 = itemService.getItemById(16);
        Item ingr4 = itemService.getItemById(17);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(1.5f, map.get(ingr3).floatValue());
        assertEquals(1.8f, map.get(ingr4).floatValue());
    }

    @Sql(value = "/sql/ingredients/withPlateDecoration.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemWithPlateDecorationTest() {
        Item item = itemService.getItemById(10);
        Item decoration = itemService.getItemById(14);
        Item ingr1 = itemService.getItemById(15);
        Item ingr2 = itemService.getItemById(16);
        Item ingr3 = itemService.getItemById(17);
        Item ingr4 = itemService.getItemById(18);
        Item tomato = itemService.getItemById(19);
        Item cucumber = itemService.getItemById(20);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(0.07f, map.get(ingr1).floatValue());
        assertEquals(0.12f, map.get(ingr2).floatValue());
        assertEquals(0.1f, map.get(ingr3).floatValue());
        assertEquals(0.175f, map.get(ingr4).floatValue());
        assertEquals(0.02f, map.get(tomato).floatValue());
        assertEquals(0.03f, map.get(cucumber).floatValue());
    }

    @Sql(value = "/sql/ingredients/withPlateDecorationInWeightDish.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemWithPlateDecorationInWeightDishTest() {
        Item item = itemService.getItemById(10);
        Item decoration = itemService.getItemById(14);
        Item weightItem = itemService.getItemById(11);
        HoldDocumentException exception = assertThrows(HoldDocumentException.class,
                () -> ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now()));
        assertEquals(
                String.format(Constants.PORTION_ITEM_MESSAGE, decoration.getName(), weightItem.getName()),
                exception.getMessage());
    }
}
