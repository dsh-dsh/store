package com.example.store.services;

import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
public class LotMoveServiceTest {

    @Autowired
    private LotMoveService lotMoveService;
    @Autowired
    private LotMoveRepository lotMoveRepository;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private LotService lotService;

    @Sql(value = "/sql/lotMovements/addDocAndLots.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/sql/lotMovements/after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addPlusLotMovementsTest() {
        ItemDoc itemDoc = documentService.getDocumentById(1);

    }

//    Map<Lot, Float> getLotMap(ItemDoc doc) {
//        Storage storage = doc.getStorageTo();
//        LocalDateTime time = doc.getDateTime();
//        doc.getDocumentItems().stream()
//                .flatMap(docItem -> {
//                    Map<Lot, Float> map = lotService.getLotMap(docItem, storage, time);
//                })
//
//    }

}
