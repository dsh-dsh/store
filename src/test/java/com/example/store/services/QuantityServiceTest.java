package com.example.store.services;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import com.example.store.repositories.IngredientRepository;
import com.example.store.repositories.QuantityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.HttpRetryException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
public class QuantityServiceTest {

    @Autowired
    private QuantityService quantityService;
    @Autowired
    private QuantityRepository quantityRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

//    todo fixit
//    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void setQuantitiesTest() {
//        LocalDate date = LocalDate.now();
//        Ingredient ingredient = ingredientRepository.getById(7);
//        List<QuantityDTO> dtoList = List.of(
//                getQuantityDTO("NET", 4f, convertDate(date)),
//                getQuantityDTO("GROSS", 2f, convertDate(date)));
//        quantityService.setQuantities(ingredient, dtoList);
//        List<Quantity> list = quantityService.getQuantityList(ingredient, date);
//        assertEquals(4, list.size());
//        assertEquals(QuantityType.NET, list.get(0).getType());
//        assertEquals(4f, list.get(0).getQuantity());
//        assertEquals(QuantityType.GROSS, list.get(1).getType());
//        assertEquals(2f, list.get(1).getQuantity());
//    }
//    todo fixit
//    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
//    @Test
//    void updateQuantitiesTest() {
//        LocalDate date = LocalDate.now();
//        Ingredient ingredient = ingredientRepository.getById(7);
//        List<QuantityDTO> dtoList = List.of(
//                getQuantityDTO("NET", 4f, convertDate(date)),
//                getQuantityDTO("GROSS", 1f, convertDate(date)));
//        quantityService.updateQuantities(ingredient, dtoList);
//        List<Quantity> list = quantityService.getQuantityList(ingredient, date);
//        assertEquals(3, list.size());
//        assertEquals(QuantityType.NET, list.get(0).getType());
//        assertEquals(4f, list.get(0).getQuantity());
//        assertEquals(QuantityType.GROSS, list.get(2).getType());
//        assertEquals(1f, list.get(2).getQuantity());
//    }

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void softDeleteQuantitiesTest() {
        LocalDate date = LocalDate.now();
        Ingredient ingredient = ingredientRepository.getById(7);
        quantityService.softDeleteQuantities(ingredient, date);
        List<Quantity> list = quantityService.getQuantityList(ingredient, date);
        assertEquals(2, list.size());
        assertTrue(list.get(0).isDeleted());
        assertTrue(list.get(1).isDeleted());
    }

    private QuantityDTO getQuantityDTO(String type, float quantity, long date) {
        QuantityDTO dto = new QuantityDTO();
        dto.setType(type);
        dto.setQuantity(quantity);
        dto.setDate(date);
        return dto;
    }

    private long convertDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
