package com.example.store.services;

import com.example.store.components.PeriodicValuesCache;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.repositories.IngredientRepository;
import com.example.store.repositories.PeriodicValueRepository;
import com.example.store.utils.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PeriodicValueServiceTest {

    @Autowired
    private PeriodicValueService periodicValueService;
    @Autowired
    private PeriodicValueRepository periodicValueRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private PeriodicValuesCache periodicValuesCache;

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setQuantitiesTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(7);
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setNetto(getPeriodicValueDTO(0,"NET", 4f, convertDate(date)));
        ingredientDTO.setGross(getPeriodicValueDTO(0, "GROSS", 2f, convertDate(date)));
        ingredientDTO.setEnable(getPeriodicValueDTO(0, "ENABLE", 1f, convertDate(date)));
        periodicValueService.setQuantities(ingredient, ingredientDTO);
        List<PeriodicValue> list = periodicValueService.getQuantityList(ingredient, date);
        assertEquals(3, list.size());
        assertEquals(PeriodicValueType.NET, list.get(0).getType());
        assertEquals(4f, list.get(0).getQuantity());
        assertEquals(PeriodicValueType.GROSS, list.get(1).getType());
        assertEquals(2f, list.get(1).getQuantity());
        assertEquals(PeriodicValueType.ENABLE, list.get(2).getType());
        assertEquals(1f, list.get(2).getQuantity());
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateQuantitiesSetNewIfNewDateTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(7);
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setNetto(getPeriodicValueDTO(19, "NET", 4f, convertDate(date)));
        ingredientDTO.setGross(getPeriodicValueDTO(20, "GROSS", 2f, convertDate(date)));
        ingredientDTO.setEnable(getPeriodicValueDTO(21, "ENABLE", 0, convertDate(date)));
        periodicValueService.updateQuantities(ingredient, ingredientDTO);
        List<PeriodicValue> list = periodicValueService.getQuantityList(ingredient, date);
        assertEquals(3, list.size());
        assertEquals(PeriodicValueType.NET, list.get(0).getType());
        assertEquals(4f, list.get(0).getQuantity());
        assertEquals(PeriodicValueType.GROSS, list.get(1).getType());
        assertEquals(2f, list.get(1).getQuantity());
        assertEquals(PeriodicValueType.ENABLE, list.get(2).getType());
        assertEquals(0f, list.get(2).getQuantity());
        List<PeriodicValue> values = periodicValueRepository.findByIngredient(ingredient);
        assertEquals(5, values.size());
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateQuantitiesUpdateIfExistingDateTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.parse("2022-02-02");
        Ingredient ingredient = ingredientRepository.getById(7);
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setNetto(getPeriodicValueDTO(19, "NET", 4f, convertDate(date)));
        ingredientDTO.setGross(getPeriodicValueDTO(20, "GROSS", 2f, convertDate(date)));
        ingredientDTO.setEnable(getPeriodicValueDTO(21, "ENABLE", 0, convertDate(date)));
        periodicValueService.updateQuantities(ingredient, ingredientDTO);
        List<PeriodicValue> list = periodicValueService.getQuantityList(ingredient, date);
        assertEquals(3, list.size());
        assertEquals(PeriodicValueType.NET, list.get(0).getType());
        assertEquals(4f, list.get(0).getQuantity());
        assertEquals(PeriodicValueType.GROSS, list.get(1).getType());
        assertEquals(2f, list.get(1).getQuantity());
        assertEquals(PeriodicValueType.ENABLE, list.get(2).getType());
        assertEquals(0, list.get(2).getQuantity());
        assertEquals(3, periodicValueRepository.findByIngredient(ingredient).size());
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteQuantitiesTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(7);
        periodicValueService.softDeleteQuantities(ingredient, date);
        List<PeriodicValue> list = periodicValueService.getQuantityList(ingredient, date);
        assertEquals(3, list.size());
        assertTrue(list.get(0).isDeleted());
        assertTrue(list.get(1).isDeleted());
        assertTrue(list.get(2).isDeleted());
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getGrossQuantityTest() {
        periodicValuesCache.setValues();
        Ingredient ingredient = ingredientRepository.getById(3);
        float value = periodicValueService.getGrossQuantity(ingredient);
        assertEquals(1.2, value, 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getGrossQuantityFalseTest() {
        periodicValuesCache.setValues();
        Ingredient ingredient = ingredientRepository.getById(333);
        float value = periodicValueService.getGrossQuantity(ingredient);
        assertEquals(0f, value);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNetQuantityTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(3);
        Optional<PeriodicValue> optional = periodicValueService.getNetQuantity(ingredient, date);
        assertTrue(optional.isPresent());
        assertEquals(1, optional.get().getQuantity(), 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getNetQuantityFalseTest() {
        periodicValuesCache.setValues();
        LocalDate date = LocalDate.parse("2022-01-01");
        Ingredient ingredient = ingredientRepository.getById(3);
        Optional<PeriodicValue> optional = periodicValueService.getNetQuantity(ingredient, date);
        assertFalse(optional.isPresent());
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getEnableQuantityTest() {
        periodicValuesCache.setValues();
        Ingredient ingredient = ingredientRepository.getById(3);
        float value = periodicValueService.getEnableQuantity(ingredient);
        assertEquals(1, value, 0.0001);
    }

    @Sql(value = "/sql/ingredients/setIngredients.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getEnableQuantityFalseTest() {
        periodicValuesCache.setValues();
        Ingredient ingredient = ingredientRepository.getById(333);
        float value = periodicValueService.getEnableQuantity(ingredient);
        assertEquals(0, value);
    }

    private PeriodicValueDTO getPeriodicValueDTO(int id, String type, float quantity, long date) {
        PeriodicValueDTO dto = new PeriodicValueDTO();
        dto.setId(id);
        dto.setType(type);
        dto.setQuantity(quantity);
        dto.setDate(date);
        return dto;
    }

    private long convertDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
