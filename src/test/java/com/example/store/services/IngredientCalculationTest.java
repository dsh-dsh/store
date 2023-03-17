package com.example.store.services;

import com.example.store.components.IngredientCalculation;
import com.example.store.components.PeriodicValuesCache;
import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.repositories.IngredientRepository;
import com.example.store.utils.Constants;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IngredientCalculationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientCalculation ingredientCalculation;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private PeriodicValuesCache periodicValuesCache;

    @Sql(value = "/sql/ingredients/addAllIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    @Order(1)
    void setTestData() {
        LocalDateTime dateTime = LocalDate.of(2022, 2, 3).atStartOfDay();
        periodicValuesCache.setValues(dateTime);
    }

    @Test
    @Transactional
    @Order(2)
    void getIngredientMapOfItemSmashPotatoTest() {
        Item item = itemService.getItemById(10);
        Item potato = itemService.getItemById(12);
        Item milk = itemService.getItemById(13);
        Item butter = itemService.getItemById(14);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(0.196).setScale(3, RoundingMode.HALF_EVEN), map.get(potato));
        assertEquals(BigDecimal.valueOf(0.047).setScale(3, RoundingMode.HALF_EVEN), map.get(milk));
        assertEquals(BigDecimal.valueOf(0.016).setScale(3, RoundingMode.HALF_EVEN), map.get(butter));
    }

    @Test
    @Transactional
    @Order(3)
    void getIngredientMapOfItemThreeStepsTest() {
        LocalDate date = LocalDate.of(2002, 2, 3);
        Item item = itemService.getItemById(15);
        Item ingr1 = itemService.getItemById(19);
        Item ingr2 = itemService.getItemById(20);
        Item ingr3 = itemService.getItemById(21);
        Item ingr4 = itemService.getItemById(22);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(1.8f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(1.5f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
    }

    @Test
    @Transactional
    @Order(4)
    void getIngredientMapOfItemBearTest() {
        Item bear = itemService.getItemById(23);
        Item bearIngredient = itemService.getItemById(24);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(bear, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(0.3F).setScale(3, RoundingMode.HALF_EVEN), map.get(bearIngredient));
    }

    @Test
    @Transactional
    @Order(5)
    void getIngredientMapOfItemBeverageTest() {
        Item bear = itemService.getItemById(25);
        Item bearIngredient = itemService.getItemById(26);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(bear, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(1F).setScale(3, RoundingMode.HALF_EVEN), map.get(bearIngredient));
    }

    @Test
    @Transactional
    @Order(6)
    void getIngredientMapOfItemSmallValuesTest() {
        Item item = itemService.getItemById(27);
        Item ingr1 = itemService.getItemById(31);
        Item ingr2 = itemService.getItemById(32);
        Item ingr3 = itemService.getItemById(33);
        Item ingr4 = itemService.getItemById(34);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(0.187).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.320).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(0.450).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.540).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
    }

    @Test
    @Transactional
    @Order(7)
    void getIngredientMapOfItemItemIsWeightTest() {
        Item item = itemService.getItemById(28);
        Item ingr3 = itemService.getItemById(33);
        Item ingr4 = itemService.getItemById(34);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(1.5f, map.get(ingr3).floatValue());
        assertEquals(1.8f, map.get(ingr4).floatValue());
    }

    @Test
    @Transactional
    @Order(8)
    void getIngredientMapOfItemWithPlateDecorationTest() {
        Item item = itemService.getItemById(35);
        Item decoration = itemService.getItemById(39);
        Item ingr1 = itemService.getItemById(40);
        Item ingr2 = itemService.getItemById(41);
        Item ingr3 = itemService.getItemById(42);
        Item ingr4 = itemService.getItemById(43);
        Item tomato = itemService.getItemById(44);
        Item cucumber = itemService.getItemById(45);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(0.070).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(0.120).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(0.100).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.175).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
        assertEquals(BigDecimal.valueOf(0.020).setScale(3, RoundingMode.HALF_EVEN), map.get(tomato));
        assertEquals(BigDecimal.valueOf(0.030).setScale(3, RoundingMode.HALF_EVEN), map.get(cucumber));
    }

    @Test
    @Transactional
    @Order(9)
    void getIngredientMapOfItemWithPlateDecorationInWeightDishTest() {
        Item item = itemService.getItemById(46);
        Item decoration = itemService.getItemById(39);
        Item weightItem = itemService.getItemById(36);
        HoldDocumentException exception = assertThrows(HoldDocumentException.class,
                () -> ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now()));
        assertEquals(
                String.format(Constants.PORTION_ITEM_MESSAGE, decoration.getName(), weightItem.getName()),
                exception.getMessage());
    }

    @Test
    @Transactional
    @Order(10)
    void getIngredientMapOfItemWhenMergingTest() {
        Item item = itemService.getItemById(57);
        Item ingr1 = itemService.getItemById(19);
        Item ingr2 = itemService.getItemById(20);
        Item ingr3 = itemService.getItemById(21);
        Item ingr4 = itemService.getItemById(22);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), LocalDate.now());
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(3.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(3.0f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(1.2f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(1.2f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
    }

    @Test
    @Order(11)
    void getIngredientsNotDeletedTest() {
        Item item = itemService.getItemById(61);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        assertEquals(2, ingredients.size());
    }

    @Test
    @Transactional
    @Order(12)
    void isWeighTrueTest() {
        Item item = itemService.getItemById(4);
        assertTrue(ingredientCalculation.isWeight(item));
    }

    @Test
    @Transactional
    @Order(13)
    void isWeighFalseTest() {
        Item item = itemService.getItemById(9);
        assertFalse(ingredientCalculation.isWeight(item));
    }

    @Test
    @Order(14)
    void getNetQuantityTest() {
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(0.8, ingredientCalculation.getNetQuantity(ingredient, date), 0.0001);
    }

    @Test
    @Order(15)
    void getNetQuantityFalseTest() {
        LocalDate date = LocalDate.parse("2022-01-01");
        Ingredient ingredient = ingredientRepository.getById(3);
        assertEquals(0, ingredientCalculation.getNetQuantity(ingredient, date), 0.0001);
    }

    @Test
    @Order(16)
    void getTotalWeightTest() {
        Item item = itemService.getItemById(61);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        assertEquals(2f, ingredientCalculation.getTotalWeight(ingredients));
    }

    @Test
    @Transactional
    @Order(17)
    void checkForPortionItemInWeightItemTest() {
        Item item = itemService.getItemById(61);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        assertDoesNotThrow(
                () -> ingredientCalculation.checkForPortionItemInWeightItem(true, ingredients.get(0)));
    }

//    @Test
//    @Transactional
//    @Order(18)
//    void checkForPortionItemInWeightItemTrueTest() {
//        LocalDate date = LocalDate.of(2022, 2, 3);
//        Item item = itemService.getItemById(61);
//        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
//        boolean isWeight = ingredientCalculation.isWeight(item);
//        assertThrows(HoldDocumentException.class,
//                () -> ingredientCalculation.checkForPortionItemInWeightItem(isWeight, ingredients.get(2)));
//    }

    @Test
    @Transactional
    @Order(19)
    void getIngredientMapOfItemThenEnableIngredientTest() {
        LocalDate date = LocalDate.of(2022, 3, 4);
        periodicValuesCache.setValues(date.atStartOfDay());
        Item item = itemService.getItemById(15);
        Item ingr1 = itemService.getItemById(19);
        Item ingr2 = itemService.getItemById(20);
        Item ingr3 = itemService.getItemById(21);
        Item ingr4 = itemService.getItemById(22);
        Map<Item, BigDecimal> map = ingredientCalculation.getIngredientMapOfItem(item, BigDecimal.valueOf(1f), date);
        assertFalse(map.isEmpty());
        assertEquals(BigDecimal.valueOf(1.5f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr1));
        assertEquals(BigDecimal.valueOf(1.8f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr2));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr3));
        assertEquals(BigDecimal.valueOf(0.6f).setScale(3, RoundingMode.HALF_EVEN), map.get(ingr4));
    }

    @Test
    @Order(20)
    void getIngredientsNotDeletedIfEnableTest() {
        Item item = itemService.getItemById(61);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        assertTrue(ingredients.isEmpty());
    }

//    @Test
//    @Order(21)
//    void getTotalWeightIfOneEnabledIngredientTest() {
//        LocalDate date = LocalDate.of(2022, 3, 4);
//        Item item = itemService.getItemById(61);
//        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item, date);
//        assertEquals(1f, ingredientCalculation.getTotalWeight(ingredients, date));
//    }

    @Test
    @Order(22)
    void getTotalWeightIfNoEnabledIngredientsTest() {
        Item item = itemService.getItemById(61);
        List<Ingredient> ingredients = ingredientCalculation.getIngredientsNotDeleted(item);
        assertThrows(BadRequestException.class, () -> ingredientCalculation.getTotalWeight(ingredients));
    }


    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Order(23)
    void deleteTestData() {
    }
}
