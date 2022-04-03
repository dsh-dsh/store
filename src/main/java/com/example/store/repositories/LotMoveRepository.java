package com.example.store.repositories;

import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LotMoveRepository extends JpaRepository<LotMovement, Long> {

    @Query("SELECT lot FROM Lot AS lot " +
            "WHERE lot = :lot AND quantity > 0")
    Optional<LotMovement> findPlusLotMovement(Lot lot);

    List<LotMovement> findByLot(Lot lot);

}
