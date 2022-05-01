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
class LotServiceMockedTest {

    @InjectMocks
    private LotService lotService;
    @Mock
    private LotMoveRepository lotMoveRepository;
    @Mock
    private DocItemService docItemService;
    @Mock
    private IngredientService ingredientService;

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

}
