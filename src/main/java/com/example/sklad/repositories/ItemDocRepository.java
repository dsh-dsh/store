package com.example.sklad.repositories;

import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemDocRepository extends JpaRepository<ItemDoc, Long> {

    @Query(value = "SELECT doc.number " +
            "FROM item_doc AS doc " +
            "WHERE doc.type = :type " +
            "ORDER BY doc.number ASC " +
            "LIMIT 1"
            , nativeQuery = true)
    int getLastNumber(DocumentType type);

}
