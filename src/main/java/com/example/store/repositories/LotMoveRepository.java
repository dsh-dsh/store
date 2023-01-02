package com.example.store.repositories;

import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import com.example.store.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LotMoveRepository extends JpaRepository<LotMovement, Long> {

    List<LotMovement> findByLot(Lot lot);

    LotMovement findByLotAndDocument(Lot lot, ItemDoc document);

    void deleteByDocument(ItemDoc document);

    // method for SerialUnHoldDocService
    @Modifying
    @Query(value = "delete from lot_movement where movement_time >= :from", nativeQuery = true)
    void deleteLotMovements(LocalDateTime from);

}
