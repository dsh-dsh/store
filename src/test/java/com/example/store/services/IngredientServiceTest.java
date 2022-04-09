package com.example.store.services;

import com.example.store.model.entities.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class IngredientServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private IngredientService ingredientService;

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getIngredientMapOfItemTest() {
        Item item = itemService.getItemById(10);
        Map<Item, Float> map = ingredientService.getIngredientMapOfItem(item, LocalDate.now());
        assertFalse(map.isEmpty());
        assertThat(map, hasValue(equalTo(1.5f)));
        assertThat(map, hasValue(equalTo(1.8f)));
        assertThat(map, hasValue(equalTo(2.4f)));
    }
}