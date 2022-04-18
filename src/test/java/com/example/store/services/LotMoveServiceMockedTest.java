package com.example.store.services;

import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.repositories.LotMoveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LotMoveServiceMockedTest {

    @InjectMocks
    private LotMoveService lotMoveService;
    @Mock
    private LotMoveRepository lotMoveRepository;

    @Test
    void getLotMovementsTest() {
        Lot lot = mock(Lot.class);
        List<LotMovement> movements
                = List.of(mock(LotMovement.class), mock(LotMovement.class));
        when(lotMoveRepository.findByLot(any(Lot.class)))
                .thenReturn(movements);
        assertEquals(2, lotMoveService.getLotMovements(lot).size());
    }

}
