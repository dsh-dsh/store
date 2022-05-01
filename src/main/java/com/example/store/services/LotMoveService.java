package com.example.store.services;

import com.example.store.exceptions.UnHoldDocumentException;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LotMoveService {

    @Autowired
    private LotMoveRepository lotMoveRepository;

    Map<String, String> map = new HashMap<>();

    //TODO add test
    public void addPlusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addPlusLotMovement(key, document, value));
    }

    //TODO add test
    public void addPlusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, doc.getDateTime(), doc.getStorageTo(), quantity);
        lotMoveRepository.save(movement);
    }

    public void updatePlusLotMovement(Lot lot, ItemDoc itemDoc, float quantity) {
        LotMovement lotMovement = lotMoveRepository.findByLotAndDocument(lot, itemDoc);
        lotMovement.setQuantity(quantity);
        lotMoveRepository.save(lotMovement);
    }

    //TODO add test
    public void addMinusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addMinusLotMovement(key, document, value));
    }

    //TODO add test
    public void addMinusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, doc.getDateTime(), doc.getStorageFrom(), quantity * -1);
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

    public void removeByDocument(ItemDoc document) {
        lotMoveRepository.deleteByDocument(document);
    }



    public List<LotMovement> getAll() {
        return lotMoveRepository.findAll();
    }

}
