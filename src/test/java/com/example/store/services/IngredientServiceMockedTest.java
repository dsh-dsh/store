package com.example.store.services;

import com.example.store.mappers.IngredientMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.enums.QuantityType;
import com.example.store.repositories.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class IngredientServiceMockedTest {

    @InjectMocks
    private IngredientService ingredientService;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private QuantityService quantityService;
    @Mock
    private IngredientMapper ingredientMapper;
    @Captor
    ArgumentCaptor<Ingredient> ingredientArgumentCaptor;
    @Captor
    ArgumentCaptor<IngredientDTO> ingredientDTOArgumentCaptor;

    private static final long LONG_DATE = 1646082000000L; // 2022-03-01

    @Test
    void setNullIngredientsTest() {
        Item item = mock(Item.class);
        ingredientService.setIngredients(item, null);
        verify(ingredientRepository, never()).save(ingredientArgumentCaptor.capture());
    }

    @Test
    void softDeleteIngredientsTest() {
        Item item = new Item(10);
        List<Ingredient> ingredients
                = List.of(
                        new Ingredient(1, null),
                        new Ingredient(2, null),
                        new Ingredient(3, null));
        when(ingredientRepository.findByParentAndIsDeleted(item, false))
                .thenReturn(ingredients);
        ingredientService.softDeleteIngredients(item, LocalDate.now());
        verify(ingredientRepository, times(3)).save(ingredientArgumentCaptor.capture());
        verify(quantityService, times(3))
                .softDeleteQuantities(ingredientArgumentCaptor.capture(), eq(LocalDate.now()));
    }

    @Test
    void getIdIngredientMapTest() {
        Item item = new Item(10);
        List<Ingredient> ingredients
                = List.of(
                        new Ingredient(1, new Item(1)),
                        new Ingredient(2, new Item(2)),
                        new Ingredient(3, new Item(3)));
        when(ingredientRepository.findByParentAndIsDeleted(item, false))
                .thenReturn(ingredients);
        Map<Integer, Ingredient> map = ingredientService.getIdIngredientMap(item);
        assertFalse(map.isEmpty());
        assertThat(map, hasKey(1));
        assertEquals(1, map.get(1).getChild().getId());
        assertThat(map, hasKey(2));
        assertEquals(2, map.get(2).getChild().getId());
        assertThat(map, hasKey(3));
        assertEquals(3, map.get(3).getChild().getId());
    }
//    todo fixit
//    @Test
//    void updateIngredientTest() {
//        Ingredient ingredient = mock(Ingredient.class);
//        IngredientDTO dto = new IngredientDTO();
//        dto.setDeleted(false);
//        List<QuantityDTO> quantities = List.of(
//                getQuantityDTO(QuantityType.GROSS, 1f, LONG_DATE),
//                getQuantityDTO(QuantityType.NET, 1.5f, LONG_DATE));
//        dto.setQuantityList(quantities);
//        ingredientService.updateIngredient(ingredient, dto);
//        verify(ingredientRepository, times(1))
//                .save(ingredientArgumentCaptor.capture());
//        verify(quantityService, times(1))
//                .updateQuantities(ingredient, dto.getQuantityList());
//    }

    @Test
    void haveIngredientsTest() {
        Item item = new Item();
        when(ingredientRepository.existsByParentAndIsDeleted(item, false))
                .thenReturn(true);
        boolean haveIngredients = ingredientService.haveIngredients(item);
        assertTrue(haveIngredients);
    }

    private QuantityDTO getQuantityDTO(QuantityType type, float quantity, long date) {
        QuantityDTO quantityDTO = new QuantityDTO();
        quantityDTO.setType(type.toString());
        quantityDTO.setQuantity(quantity);
        quantityDTO.setDate(date);
        return quantityDTO;
    }

    private long convertDate(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
