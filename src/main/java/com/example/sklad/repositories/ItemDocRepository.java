package com.example.sklad.repositories;

import com.example.sklad.model.entities.documents.ItemDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemDocRepository extends JpaRepository<ItemDoc, Integer> {

    @Query(value = "SELECT number " +
            "FROM document " +
            "WHERE doc_type = :docType " +
            "ORDER BY number DESC " +
            "LIMIT 1"
            , nativeQuery = true)
    int getLastNumber(String docType);

    Optional<ItemDoc> findByNumber(int number);

    List<ItemDoc> findByDocType(DocumentType docType);

}