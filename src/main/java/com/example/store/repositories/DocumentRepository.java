package com.example.store.repositories;

import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("SELECT doc " +
            "FROM Document doc " +
            "WHERE (:docType is null OR doc.docType = :docType)")
    Page<Document> getByDocType(DocumentType docType, Pageable pageable);

    List<Document> findByDocType(DocumentType docType);
}
