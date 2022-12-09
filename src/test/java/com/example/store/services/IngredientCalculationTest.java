package com.example.store.services;

import com.example.store.components.IngredientCalculation;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.repositories.IngredientRepository;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
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
    @Autowired
    private IngredientRepository ingredientRepository;

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
        assertEquals(BigDecimal.valueOf(0.196).setScale(3, RoundingMode.HALF_EVEN), map.get(potato));
        assertEquals(BigDecimal.valueOf(0.047).setScale(3, RoundingMode.HALF_EVEN), map.get(milk));
        assertEquals(BigDecimal.valueOf(0.016).setScale(3, RoundingMode.HALF_EVEN), map.get(butter));
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
        assertEquals(BigDecimal.valueOf(1.8f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(1.5f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
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
        assertEquals(BigDecimal.valueOf(0.3F).setScale(3, RoundingMode.HALF_EVEN), map.get(bearIngredient));
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
        assertEquals(BigDecimal.valueOf(1F).setScale(3, RoundingMode.HALF_EVEN), map.get(bearIngredient));
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
        assertEquals(BigDecimal.valueOf(0.187).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.320).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(0.450).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.540).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
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
        assertEquals(BigDecimal.valueOf(0.070).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.120).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(0.100).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.175).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
        assertEquals(BigDecimal.valueOf(0.020).setScale(3, RoundingMode.HALF_EVEN), map.get(tomato));
        assertEquals(BigDecimal.valueOf(0.030).setScale(3, RoundingMode.HALF_EVEN), map.get(cucumber));
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

    @Sql(value = {"/sql/ingredients/threeStepsIngredients.sql", "/sql/ingredients/oneEnableIngredient.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemThenEnableIngredientTest() {
        Item item = itemService.getItemById(10);
        Item ingr1 = itemService.getItemById(14);
        Item ingr2 = itemService.getItemById(15);
        Item ingr3 = itemService.getItemById(16);
        Item ingr4 = itemService.getItemById(17);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(1.8f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(1.5f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(1.2f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
    }

    @Sql(value = {"/sql/ingredients/threeStepsIngredients.sql", "/sql/ingredients/addTwoIngredientsItem.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIngredientMapOfItemWhenMergingTest() {
        Item item = itemService.getItemById(18);
        Item ingr1 = itemService.getItemById(14);
        Item ingr2 = itemService.getItemById(15);
        Item ingr3 = itemService.getItemById(16);
        Item ingr4 = itemService.getItemById(17);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(3.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(3.0f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(1.2f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(1.2f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getIngredientsNotDeletedTest() {
        Item item = itemService.getItemById(13);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, LocalDate.now());
        assertEquals(2, ingredients.size());
    }

    @Sql(value = {"/sql/ingredients/setIngredients.sql", "/sql/ingredients/enableIngredients.sql"}
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getIngredientsNotDeletedIfEnableTest() {
        Item item = itemService.getItemById(13);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, LocalDate.now());
        assertTrue(ingredients.isEmpty());
    }

    @Test
    @Transactional
    void isWeighTrueTest() {
        Item item = itemService.getItemById(4);
        assertTrue(ingredientCalculation.isWeight(item));
    }

    @Test
    @Transactional
    void isWeighFalseTest() {
        Item item = itemService.getItemById(9);
        assertFalse(ingredientCalculation.isWeight(item));
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getGrossQuantityTest() {
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(1.2, ingredientCalculation.getGrossQuantity(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getGrossQuantityFalseTest() {
        LocalDate date = LocalDate.parse("2022-01-01");
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(0, ingredientCalculation.getGrossQuantity(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNetQuantityTest() {
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(1, ingredientCalculation.getNetQuantity(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNetQuantityFalseTest() {
        LocalDate date = LocalDate.parse("2022-01-01");
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(0, ingredientCalculation.getNetQuantity(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getEnableQuantityTest() {
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(1, ingredientCalculation.getEnableValue(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getEnableQuantityFalseTest() {
        LocalDate date = LocalDate.parse("2022-01-01");
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(0, ingredientCalculation.getEnableValue(ingredient, date), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getTotalWeightTest() {
        LocalDate date = LocalDate.now();
        Item item = itemService.getItemById(13);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
        assertEquals(2f, ingredientCalculation.getTotalWeight(ingredients, date));
    }

    @Sql(value = {"/sql/ingredients/setIngredients.sql", "/sql/ingredients/oneEnableIngredient.sql"}
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getTotalWeightIfOneEnabledIngredientTest() {
        LocalDate date = LocalDate.now();
        Item item = itemService.getItemById(13);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
        assertEquals(1f, ingredientCalculation.getTotalWeight(ingredients, date));
    }

    @Sql(value = {"/sql/ingredients/setIngredients.sql", "/sql/ingredients/enableIngredients.sql"}
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getTotalWeightIfNoEnabledIngredientsTest() {
        LocalDate date = LocalDate.now();
        Item item = itemService.getItemById(13);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
        assertThrows(BadRequestException.class, () -> ingredientCalculation.getTotalWeight(ingredients, date));
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkForPortionItemInWeightItemTest() {
        Item item = itemService.getItemById(13);
        LocalDate date = LocalDate.now();
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
        assertDoesNotThrow(
                () -> ingredientCalculation.checkForPortionItemInWeightItem(true, ingredients.get(0)));
    }

    @Sql(value = {"/sql/ingredients/setIngredients.sql", "/sql/ingredients/setNotWeightIngredient.sql"}
            , executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void checkForPortionItemInWeightItemTrueTest() {
        Item item = itemService.getItemById(13);
        LocalDate date = LocalDate.now();
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
        boolean isWeight = ingredientCalculation.isWeight(item);
        assertThrows(HoldDocumentException.class,
                () -> ingredientCalculation.checkForPortionItemInWeightItem(isWeight, ingredients.get(2)));
    }
}
