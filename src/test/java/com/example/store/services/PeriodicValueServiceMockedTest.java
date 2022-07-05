package com.example.store.services;

import com.example.store.model.dto.PeriodicValueDTO;
import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PeriodicValueServiceMockedTest {

//    @InjectMocks
    @Mock
    private PeriodicValueService periodicValueService;
    @Mock
    private Ingredient ingredient;
    private LocalDate date;
    private final List<PeriodicValue> quantities = List.of(
            getQuantity(1.95f, PeriodicValueType.NET, LocalDate.parse("2022-03-01")),
            getQuantity(1.7f, PeriodicValueType.NET, LocalDate.parse("2022-02-01")),
            getQuantity(1.4f, PeriodicValueType.NET, LocalDate.parse("2022-01-01")),
            getQuantity(1f, PeriodicValueType.GROSS, LocalDate.parse("2022-01-01")));

    @Test
    void getQuantityRatioTest() {
        doReturn(quantities).when(periodicValueService).getQuantityList(ingredient, date);
        float ratio = periodicValueService.getQuantityRatio(ingredient, date);
        assertEquals(1.95f, ratio);
    }

    @Test
    void getGrossQuantityTest() {
        doReturn(quantities).when(periodicValueService).getQuantityList(ingredient, date);
        float quantity = periodicValueService.getGrossQuantity(ingredient, date).get().getQuantity();
        assertEquals(1f, quantity);
    }

    @Test
    void getQuantityDTOListTest() {
        doReturn(quantities).when(periodicValueService).getQuantityList(ingredient, date);
        List<PeriodicValueDTO> list = periodicValueService.getQuantityDTOList(ingredient, date);
        assertEquals(4, list.size());
    }

    @Test
    void getQuantityListTest() {
        doReturn(quantities).when(periodicValueService).getQuantityList(ingredient, date);
        List<PeriodicValue> list = periodicValueService.getQuantityList(ingredient, date);
        assertEquals(4, list.size());
    }

    private PeriodicValue getQuantity(float value, PeriodicValueType type, LocalDate date) {
        PeriodicValue periodicValue = new PeriodicValue();
        periodicValue.setQuantity(value);
        periodicValue.setType(type);
        periodicValue.setDate(date);
        return periodicValue;
    }
}
