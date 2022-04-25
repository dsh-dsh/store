package com.example.store.repositories;

import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("SELECT COUNT(doc) > 0 " +
            "FROM Document doc " +
            "WHERE doc.docType = :docType " +
            "AND (:storageFrom is null OR doc.storageFrom = :storageFrom) " +
            "AND (:storageTo is null OR doc.storageTo = :storageTo) " +
            "AND doc.dateTime < :dateTime " +
            "AND doc.isHold = true ")
    boolean existsNotHoldenDocs(DocumentType docType, Storage storageFrom, Storage storageTo, LocalDateTime dateTime);

    @Query("SELECT doc " +
            "FROM Document doc " +
            "WHERE (:docType is null OR doc.docType = :docType)")
    Page<Document> getByDocType(DocumentType docType, Pageable pageable);

    @Query(value = "SELECT number " +
            "FROM document " +
            "WHERE doc_type = :docType " +
            "ORDER BY number DESC " +
            "LIMIT 1"
            , nativeQuery = true)
    int getLastNumber(String docType);


}
