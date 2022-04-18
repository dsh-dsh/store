package com.example.store.services;

import com.example.store.exceptions.UnHoldDocumentException;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LotMoveService {

    @Autowired
    private LotMoveRepository lotMoveRepository;


    //TODO add test
    public void addPlusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addPlusLotMovement(key, document, value));
    }

    //TODO add test
    public void addPlusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, doc.getDateTime(), quantity);
        movement.setStorage(doc.getStorageTo());
        lotMoveRepository.save(movement);
    }

    //TODO add test
    public void addMinusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addMinusLotMovement(key, document, value));
    }

    //TODO add test
    public void addMinusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, doc.getDateTime(), quantity * -1);
        movement.setStorage(doc.getStorageFrom());
        lotMoveRepository.save(movement);
    }

    public List<LotMovement> getLotMovements(Lot lot) {
        return lotMoveRepository.findByLot(lot);
    }

    //TODO add test
    public void removeLotMovement(Lot lot) {
        List<LotMovement> movements = getLotMovements(lot);
        if(movements.size() > 1) throw new UnHoldDocumentException();
        lotMoveRepository.delete(movements.get(0));
    }

}
