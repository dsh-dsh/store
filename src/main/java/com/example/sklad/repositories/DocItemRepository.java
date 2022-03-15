package com.example.sklad.repositories;

import com.example.sklad.model.entities.DocumentItem;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocItemRepository extends JpaRepository<DocumentItem, Long> {

    @Modifying
    @Query("DELETE docItem FROM DocumentItem AS docItem " +
            "WHERE docItem IN (:docItems)")
    void deleteByIdIn(List<DocumentItem> docItems);

    List<DocumentItem> findByItemDoc(ItemDoc itemDoc);

}
