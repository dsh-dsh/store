package com.example.store.services;

import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.repositories.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
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
    @Autowired
    private IngredientRepository ingredientRepository;

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void getIdIngredientMapTest() {
        LocalDate date = LocalDate.parse("2022-03-16");
        Item item = itemService.getItemById(10);
        Item item1 = itemService.getItemById(14);
        Item item2 = itemService.getItemById(15);
        Item item3 = itemService.getItemById(16);
        Item item4 = itemService.getItemById(17);
        Map<Item, Float> itemMap = new HashMap<>();
        itemMap.put(item, 2f);
        Map<Item, Float> map = ingredientService.getIngredientMap(itemMap, date);
        assertFalse(map.isEmpty());
        assertThat(map, hasKey(item1));
        assertEquals(3f, map.get(item1));
        assertThat(map, hasKey(item2));
        assertEquals(3.6f, map.get(item2));
        assertThat(map, hasKey(item3));
        assertEquals(4.8f, map.get(item3));
        assertThat(map, hasKey(item4));
        assertEquals(4.8f, map.get(item4));
    }

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getIngredientDTOListTest() {
        LocalDate date = LocalDate.now();
        Item item = itemService.getItemById(12);
        List<IngredientDTO> list = ingredientService.getIngredientDTOList(item, date);
        assertFalse(list.isEmpty());
        assertEquals(2, list.size());
        assertEquals(14, list.get(0).getChild().getId());
        assertEquals(1.5f, list.get(0).getQuantityList().get(0).getQuantity());
        assertEquals(1f, list.get(0).getQuantityList().get(1).getQuantity());
        assertEquals(15, list.get(1).getChild().getId());
        assertEquals(1.8f, list.get(1).getQuantityList().get(0).getQuantity());
        assertEquals(1f, list.get(1).getQuantityList().get(1).getQuantity());
    }

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
                getIngredientDTO(parentId,11, 1.5f, 0),
                getIngredientDTO(parentId,12, 1.5f, 1),
                getIngredientDTO(parentId,7, 1.2f, 1));
        ingredientService.updateIngredients(item, dtoList);
        List<IngredientDTO> list = ingredientService.getIngredientDTOList(item, LocalDate.now());
        assertEquals(3, list.size());
        assertEquals(11, list.get(0).getChildId());
        assertEquals(1.5f, list.get(0).getNetto().getQuantity());
        assertEquals(0f, list.get(0).getEnable().getQuantity());
        assertEquals(12, list.get(1).getChildId());
        assertEquals(1.5f, list.get(1).getNetto().getQuantity());
        assertEquals(1f, list.get(1).getGross().getQuantity());
        assertEquals(7, list.get(2).getChildId());
        assertEquals(1.2f, list.get(2).getNetto().getQuantity());
        assertEquals(1f, list.get(2).getGross().getQuantity());
    }

    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setIngredientsTest() {
        int parentId = 6;
        Item item = itemService.getItemById(parentId);
        List<IngredientDTO> dtoList = List.of(
                getIngredientDTO(parentId,7, 1.5f, 1),
                getIngredientDTO(parentId,8, 1.2f, 1));
        ingredientService.setIngredients(item, dtoList);
        List<IngredientDTO> list = ingredientService.getIngredientDTOList(item, LocalDate.now());
        assertEquals(2, list.size());
        assertEquals(7, list.get(0).getChild().getId());
        assertEquals(1.5f, list.get(0).getQuantityList().get(0).getQuantity());
        assertEquals(8, list.get(1).getChild().getId());
        assertEquals(1.2f, list.get(1).getQuantityList().get(0).getQuantity());
    }

    @Sql(value = "/sql/ingredients/before.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/ingredients/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addInnerItemsTest() {
        ItemDoc document = new ItemDoc();
        LocalDate date = LocalDate.now();
        List<DocumentItem> dosItems = new ArrayList<>();
        dosItems.add(new DocumentItem(document, getItem(7), 1));
        dosItems.add(new DocumentItem(document, getItem(11), 1));
        dosItems.add(new DocumentItem(document, getItem(12), 2));
        ingredientService.addInnerItems(dosItems, date);
        assertEquals(5, dosItems.size());
        assertEquals(7, dosItems.get(0).getItem().getId());
        assertEquals(1f, dosItems.get(0).getQuantity());
        assertEquals(17, dosItems.get(1).getItem().getId());
        assertEquals(2.4f, dosItems.get(1).getQuantity());
        assertEquals(14, dosItems.get(2).getItem().getId());
        assertEquals(3f, dosItems.get(2).getQuantity());
        assertEquals(15, dosItems.get(3).getItem().getId());
        assertEquals(3.6f, dosItems.get(3).getQuantity());
        assertEquals(16, dosItems.get(4).getItem().getId());
        assertEquals(2.4f, dosItems.get(4).getQuantity());
    }

    Item getItem(int id) {
        Item item = new Item(id);
        item.setName("name" + id);
        return item;
    }

    private IngredientDTO getIngredientDTO(int parentId, int childId, float netQuantity, float enableQuantity) {
        long date = convertDate(LocalDate.now());
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setChildId(childId);
        ingredientDTO.setParentId(parentId);

        PeriodicValueDTO netPeriodicValueDTO = new PeriodicValueDTO();
        netPeriodicValueDTO.setType(PeriodicValueType.NET.toString());
        netPeriodicValueDTO.setQuantity(netQuantity);
        netPeriodicValueDTO.setDate(date);

        PeriodicValueDTO grossPeriodicValueDTO = new PeriodicValueDTO();
        grossPeriodicValueDTO.setType(PeriodicValueType.GROSS.toString());
        grossPeriodicValueDTO.setQuantity(1f);
        grossPeriodicValueDTO.setDate(date);

        PeriodicValueDTO enablePeriodicValueDTO = new PeriodicValueDTO();
        enablePeriodicValueDTO.setType(PeriodicValueType.ENABLE.toString());
        enablePeriodicValueDTO.setQuantity(enableQuantity);
        enablePeriodicValueDTO.setDate(date);

        ingredientDTO.setNetto(netPeriodicValueDTO);
        ingredientDTO.setGross(grossPeriodicValueDTO);
        ingredientDTO.setEnable(enablePeriodicValueDTO);
        return ingredientDTO;
    }

    private long convertDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}