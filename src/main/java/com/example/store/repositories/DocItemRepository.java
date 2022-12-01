package com.example.store.repositories;

import com.example.store.model.entities.DocumentItem;
import com.example.store.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocItemRepository extends JpaRepository<DocumentItem, Integer> {

    @Modifying
    @Query("DELETE DocumentItem AS docItem " +
            "WHERE docItem IN (:docItems)")
    void deleteByIdIn(List<DocumentItem> docItems);

    List<DocumentItem> findByItemDoc(ItemDoc itemDoc);

    @Query(value =
            "select doc_item.* from document_item as doc_item " +
            "left join document as doc on doc.id = doc_item.document_id " +
            "where doc_item.item_id = :itemId " +
            "and (:onlyHolden = false or doc.is_hold = :onlyHolden) " +
            "and (doc.storage_to_id = :storageId or doc.storage_from_id = :storageId) " +
            "and doc.date_time between :start and :end " +
            "order by doc.date_time", nativeQuery = true)
    List<DocumentItem> findByItemId(int itemId, int storageId, LocalDateTime start, LocalDateTime end, boolean onlyHolden);

    void deleteByItemDoc(ItemDoc itemDoc);

    @Query(value = "select count(*) from document_item where document_id = :docId", nativeQuery = true)
    int countItemsByDocId(int docId);

    @Query(value =
            "select doc_item.* from document_item as doc_item " +
            "left join document as doc on doc.id = doc_item.document_id " +
            "where doc.project_id = :projectId " +
            "and doc.date_time between :start and :end " +
            "and doc.doc_type = 'CHECK_DOC' " +
            "and (:onlyHolden = false or doc.is_hold = :onlyHolden)", nativeQuery = true)
    List<DocumentItem> findByPeriod(int projectId, LocalDateTime start, LocalDateTime end, boolean onlyHolden);
}
