package com.example.store.services;

import com.example.store.mappers.IngredientMapper;
import com.example.store.model.dto.IngredientDTO;
import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Quantity;
import com.example.store.repositories.IngredientRepository;
import com.example.store.repositories.QuantityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IngredientServiceMockedTest {

    @InjectMocks
    private IngredientService ingredientService;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private QuantityService quantityService;
    @Captor
    ArgumentCaptor<Ingredient> ingredientArgumentCaptor;

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
                = List.of(new Ingredient(1), new Ingredient(2), new Ingredient(3));
        when(ingredientRepository.findByParentAndIsDeleted(item, false))
                .thenReturn(ingredients);
        ingredientService.softDeleteIngredients(item, LocalDate.now());
        verify(ingredientRepository, times(3)).save(ingredientArgumentCaptor.capture());
        // TODO verify quantities deletion
//        verify(quantityService, times(3))
//                .softDeleteQuantities(ingredientArgumentCaptor.capture(), LocalDate.now());
    }

}
