package com.example.store.services;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Quantity;
import com.example.store.repositories.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

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
