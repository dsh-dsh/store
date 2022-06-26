package com.example.store.services;

import com.example.store.mappers.QuantityMapper;
import com.example.store.model.dto.QuantityDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import com.example.store.repositories.QuantityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class QuantityServiceMockedTest {

//    @InjectMocks
    @Mock
    private QuantityService quantityService;
    @Mock
    private Ingredient ingredient;
    private LocalDate date;
    private final List<Quantity> quantities = List.of(
            getQuantity(1.95f, QuantityType.NET, LocalDate.parse("2022-03-01")),
            getQuantity(1.7f, QuantityType.NET, LocalDate.parse("2022-02-01")),
            getQuantity(1.4f, QuantityType.NET, LocalDate.parse("2022-01-01")),
            getQuantity(1f, QuantityType.GROSS, LocalDate.parse("2022-01-01")));

    @Test
    void getQuantityRatioTest() {
        doReturn(quantities).when(quantityService).getQuantityList(ingredient, date);
        float ratio = quantityService.getQuantityRatio(ingredient, date);
        assertEquals(1.95f, ratio);
    }

    @Test
    void getGrossQuantityTest() {
        doReturn(quantities).when(quantityService).getQuantityList(ingredient, date);
        float quantity = quantityService.getGrossQuantity(ingredient, date).get().getQuantity();
        assertEquals(1f, quantity);
    }

    @Test
    void getQuantityDTOListTest() {
        doReturn(quantities).when(quantityService).getQuantityList(ingredient, date);
        List<QuantityDTO> list = quantityService.getQuantityDTOList(ingredient, date);
        assertEquals(4, list.size());
    }

    @Test
    void getQuantityListTest() {
        doReturn(quantities).when(quantityService).getQuantityList(ingredient, date);
        List<Quantity> list = quantityService.getQuantityList(ingredient, date);
        assertEquals(4, list.size());
    }

    private Quantity getQuantity(float value, QuantityType type, LocalDate date) {
        Quantity quantity = new Quantity();
        quantity.setQuantity(value);
        quantity.setType(type);
        quantity.setDate(date);
        return quantity;
    }
}
