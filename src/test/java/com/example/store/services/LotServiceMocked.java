package com.example.store.services;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.exceptions.NoDocumentItemsException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.repositories.LotMoveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LotServiceMocked {

    @InjectMocks
    private LotService lotService;
    @Mock
    private LotMoveRepository lotMoveRepository;
    @Mock
    private DocItemService docItemService;
    @Mock
    private IngredientService ingredientService;

    @Test
    void addLotMovementsOfReceiptDocTest() {
        ItemDoc document = mock(ItemDoc.class);
        document.setDocType(DocumentType.RECEIPT_DOC);
        List<DocumentItem> items = mock(List.class);
        when(docItemService.getItemsByDoc(document)).thenReturn(items);
        lotService.addLotMovements(document);
        verify(lotService, times(1)).addLots(document);
    }

    @Test
    void addLotMovementsOfWriteOffDocTest() {
        ItemDoc document = mock(ItemDoc.class);
        document.setDocType(DocumentType.WRITE_OFF_DOC);
        List<DocumentItem> items = new ArrayList<>();
        items.add(mock(DocumentItem.class));
        items.add(mock(DocumentItem.class));
        items.add(mock(DocumentItem.class));
        when(docItemService.getItemsByDoc(document)).thenReturn(items);
        lotService.addLotMovements(document);
        verify(ingredientService, times(1)).addInnerItems(items, LocalDate.now());
        verify(lotService, times(3)).addStorageDocMovement(mock(DocumentItem.class));
    }

    @Test
    void addLotMovementsWhenDocItemListIsEmptyTest() {
        ItemDoc document = mock(ItemDoc.class);
        document.setDocType(DocumentType.WRITE_OFF_DOC);
        List<DocumentItem> items = new ArrayList<>();
        when(docItemService.getItemsByDoc(document)).thenReturn(items);
        assertThrows(NoDocumentItemsException.class, () -> {
            lotService.addLotMovements(document);
        });
    }

    @Test
    void addStorageDocMovementTest() {

    }

}
