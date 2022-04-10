package com.example.store.services;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.QuantityType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
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

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateIngredientsTest() {
        int parentId = 10;
        Item item = itemService.getItemById(parentId);
        List<IngredientDTO> dtoList = List.of(
                getIngredientDTO(parentId,12, 1.5f),
                getIngredientDTO(parentId,7, 1.2f));
        ingredientService.updateIngredients(item, dtoList);
        List<IngredientDTO> list = ingredientService.getIngredientDTOList(item, LocalDate.now());
        assertEquals(2, list.size());
        assertEquals(12, list.get(0).getChild().getId());
        assertEquals(1.5f, list.get(0).getQuantityList().get(0).getQuantity());
        assertEquals(1f, list.get(0).getQuantityList().get(1).getQuantity());
        assertEquals(7, list.get(1).getChild().getId());
        assertEquals(1.2f, list.get(1).getQuantityList().get(0).getQuantity());
        assertEquals(1f, list.get(1).getQuantityList().get(1).getQuantity());
        List<IngredientDTO> deletedList = ingredientService.getDeletedIngredientDTOList(item, LocalDate.now());
        assertEquals(1, deletedList.size());
        assertEquals(11, deletedList.get(0).getChild().getId());
        assertTrue(deletedList.get(0).isDeleted());
        assertTrue(deletedList.get(0).getQuantityList().get(0).isDeleted());
        assertTrue(deletedList.get(0).getQuantityList().get(1).isDeleted());
    }

    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setIngredientsTest() {
        int parentId = 6;
        Item item = itemService.getItemById(parentId);
        List<IngredientDTO> dtoList = List.of(
                getIngredientDTO(parentId,7, 1.5f),
                getIngredientDTO(parentId,8, 1.2f));
        ingredientService.setIngredients(item, dtoList);
        List<IngredientDTO> list = ingredientService.getIngredientDTOList(item, LocalDate.now());
        assertEquals(2, list.size());
        assertEquals(7, list.get(0).getChild().getId());
        assertEquals(1.5f, list.get(0).getQuantityList().get(0).getQuantity());
        assertEquals(8, list.get(1).getChild().getId());
        assertEquals(1.2f, list.get(1).getQuantityList().get(0).getQuantity());
    }


    private IngredientDTO getIngredientDTO(int parentId, int childId, float netQuantity) {
        String date = LocalDate.now().toString();
        ItemDTO parent = new ItemDTO();
        parent.setParentId(parentId);
        ItemDTO child = new ItemDTO();
        child.setId(childId);
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setParent(parent);
        ingredientDTO.setChild(child);
        QuantityDTO quantityDTO1 = new QuantityDTO();
        quantityDTO1.setType(QuantityType.NET.toString());
        quantityDTO1.setQuantity(netQuantity);
        quantityDTO1.setDate(date);
        QuantityDTO quantityDTO2 = new QuantityDTO();
        quantityDTO2.setType(QuantityType.GROSS.toString());
        quantityDTO2.setQuantity(1f);
        quantityDTO2.setDate(date);
        ingredientDTO.setQuantityList(List.of(quantityDTO1, quantityDTO2));
        return ingredientDTO;
    }
}