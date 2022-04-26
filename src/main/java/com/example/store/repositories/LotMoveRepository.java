package com.example.store.repositories;

import com.example.store.model.entities.Lot;
import com.example.store.model.entities.LotMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotMoveRepository extends JpaRepository<LotMovement, Long> {

    List<LotMovement> findByLot(Lot lot);

}
