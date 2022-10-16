package com.example.store.services;

import com.example.store.exceptions.HoldDocumentException;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.utils.Constants;
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

    public void addPlusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addPlusLotMovement(key, document, value));
    }

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

    public void addMinusLotMovements(ItemDoc document, Map<Lot, Float> newLotMap) {
        newLotMap.forEach((key, value) -> addMinusLotMovement(key, document, value));
    }

    public void addMinusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, doc.getDateTime(), doc.getStorageFrom(), quantity * -1);
        lotMoveRepository.save(movement);
    }

    public List<LotMovement> getLotMovements(Lot lot) {
        return lotMoveRepository.findByLot(lot);
    }

    public void removeLotMovement(Lot lot) {
        List<LotMovement> movements = getLotMovements(lot);
        if(movements.size() > 1) {
            throw new HoldDocumentException(
                    Constants.UN_HOLD_FAILED_MESSAGE,
                    this.getClass().getName() + " - removeLotMovement(Lot lot)");
        }
        lotMoveRepository.delete(movements.get(0));
    }

    public void removeByDocument(ItemDoc document) {
        lotMoveRepository.deleteByDocument(document);
    }

    public List<LotMovement> getAll() {
        return lotMoveRepository.findAll();
    }

}
