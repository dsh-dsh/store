package com.example.sklad.repositories;

import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocItemRepository extends JpaRepository<DocumentItem, Integer> {

    @Modifying
    @Query("DELETE DocumentItem AS docItem " +
            "WHERE docItem IN (:docItems)")
    void deleteByIdIn(List<DocumentItem> docItems);

    List<DocumentItem> findByItemDoc(ItemDoc itemDoc);

    void deleteByItemDoc(ItemDoc itemDoc);

    @Query(value = "select count(*) from document_item where document_id = :docId", nativeQuery = true)
    int countItemsByDocId(int docId);
}
