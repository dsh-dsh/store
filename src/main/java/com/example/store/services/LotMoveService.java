package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.exceptions.UnHoldDocumentException;
import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.repositories.LotMoveRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LotMoveService {

    @Autowired
    private LotMoveRepository lotMoveRepository;


    public void addPlusLotMovement(Lot lot, DocumentItem item) {
        LotMovement movement = new LotMovement();
        movement.setLot(lot);
        movement.setMovementTime(LocalDateTime.now());

        ItemDoc doc = item.getItemDoc();
        movement.setDocument(doc);
        movement.setStorage(doc.getStorageTo());

        float quantity = item.getQuantity();
        float price = item.getPrice();
        movement.setQuantity(quantity);

        lotMoveRepository.save(movement);
    }
    public void addPlusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, LocalDateTime.now(), quantity);
        movement.setStorage(doc.getStorageTo());
        lotMoveRepository.save(movement);

    }

    public void addMinusLotMovement(Lot lot, ItemDoc doc, float quantity) {
        LotMovement movement =
                new LotMovement(lot, doc, LocalDateTime.now(), quantity * -1);
        movement.setStorage(doc.getStorageFrom());
        lotMoveRepository.save(movement);
    }

    public LotMovement getPlusLotMovement(Lot lot) {
        return lotMoveRepository.findPlusLotMovement(lot)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_LOT_MOVEMENT_MESSAGE));
    }

    private List<LotMovement> getLotMovements(Lot lot) {
        return lotMoveRepository.findByLot(lot);
    }

    public void removeLotMovement(Lot lot) {
        List<LotMovement> movements = getLotMovements(lot);
        if(movements.size() > 1) throw new UnHoldDocumentException();
        lotMoveRepository.delete(movements.get(0));
    }

}
