package com.example.sklad.repositories;

import com.example.sklad.model.entities.documents.Document;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("SELECT doc " +
            "FROM Document doc " +
            "WHERE (:docType is null OR doc.docType = :docType)")
    List<Document> getByDocType(DocumentType docType);
}
