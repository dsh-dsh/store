package com.example.store.repositories;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.projections.LotFloat;
import com.example.store.utils.annotations.Loggable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    Optional<Lot> findByDocument(ItemDoc document);

    @Loggable
    @Query(value = "select lot.id, sum(movement.quantity) as value from lot " +
            "left join lot_movement as movement on lot.id = movement.lot_id " +
            "where lot.item_id = :itemId and movement.storage_id = :storageId " +
            "and movement.movement_time < :time " +
            "group by lot.id " +
            "having sum(movement.quantity) > 0"
            , nativeQuery = true)
    List<LotFloat> getLotsOfItem(int itemId, int storageId, LocalDateTime time);


    Optional<Lot> findTop1ByItem(Item item, Sort sort);
}
