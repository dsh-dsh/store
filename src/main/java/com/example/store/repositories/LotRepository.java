package com.example.store.repositories;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.Lot;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.projections.LotFloat;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    Optional<Lot> findByDocumentItem(DocumentItem documentItem);

    @Query("SELECT lot " +
            "FROM Lot lot " +
            "JOIN FETCH DocumentItem docItem " +
            "WHERE docItem.itemDoc = :itemDoc")
    Optional<Lot> findByDocument(ItemDoc itemDoc);

    @Query(value = "select lot.id, sum(movement.quantity) as value from lot " +
            "left join lot_movement as movement on lot.id = movement.lot_id " +
            "left join document_item as doc_item on doc_item.id = lot.document_item_id " +
            "where doc_item.item_id = :itemId and movement.storage_id = :storageId " +
            "and movement.movement_time < :time " +
            "group by lot.id " +
            "having sum(movement.quantity) > 0", nativeQuery = true)
    List<LotFloat> getLotsOfItem(int itemId, int storageId, LocalDateTime time);

    @Query(value = "select doc_item.price " +
            "from document_item as doc_item " +
            "inner join lot on lot.document_item_id = doc_item.id " +
            "inner join item on item.id = doc_item.item_id " +
            "where item.id = :itemId " +
            "order by lot.lot_time desc " +
            "limit 1", nativeQuery = true)
    float findLastPrice(int itemId);

    @Query(value = "select sum(movement.quantity) from lot " +
            "inner join lot_movement as movement on lot.id = movement.lot_id " +
            "where lot.id = :lotId and movement.storage_id = :storageId", nativeQuery = true)
    float getQuantityRestOfLot(long lotId, int storageId);
}
