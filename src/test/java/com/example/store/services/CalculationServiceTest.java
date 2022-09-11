package com.example.store.services;

import com.example.store.model.dto.CalculationDTO;
import com.example.store.model.dto.IngredientCalculationDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CalculationServiceTest {

    @Autowired
    private CalculationService calculationService;
    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private ItemService itemService;

    private final float THRESHOLD = 0.0001f;

    @Sql(value = {"/sql/ingredients/before.sql", "/sql/ingredients/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getCalculationTest() {
        Item item = itemService.getItemById(10);
        CalculationDTO dto = calculationService.getCalculationDTO(item, LocalDate.now());
        assertEquals("Некое блюдо", dto.getItemName());
        assertEquals(200f, dto.getIngredients().get(1).getAmount());
    }

    @Sql(value = {"/sql/ingredients/before.sql", "/sql/ingredients/addLots.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCalculationForIngredientTest() {

        Ingredient ingredient = ingredientService.getIngredientById(2);
        IngredientCalculationDTO dto = calculationService.getCostCalculation(ingredient, LocalDate.now());

        assertEquals("Полуфабрикат 2", dto.getItemName());
        assertTrue(equalsFloat(200f, dto.getAmount()));
    }

    private boolean equalsFloat(float a, float b) {
        return Math.abs(a - b) < THRESHOLD;
    }
}