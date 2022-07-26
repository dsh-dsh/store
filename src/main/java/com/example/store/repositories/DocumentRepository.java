package com.example.store.repositories;

import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    boolean existsByDateTimeBeforeAndIsDeletedAndIsHold(LocalDateTime dateTime, boolean isDeleted, boolean isHold);

    boolean existsByDateTimeAfterAndIsHold(LocalDateTime dateTime, boolean isHold);

    @Query("SELECT doc " +
            "FROM Document doc " +
            "WHERE (:filter = '' OR doc.docType IN (:types)) " +
            "AND doc.dateTime BETWEEN :start AND :end")
    List<Document> findByDocInFilter(String filter, Collection<DocumentType> types, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT number " +
            "FROM document " +
            "WHERE doc_type = :docType " +
            "ORDER BY number DESC " +
            "LIMIT 1"
            , nativeQuery = true)
    int getLastNumber(String docType);

    List<Document> findByIsHoldAndDateTimeAfter(boolean isHold, LocalDateTime dateTime, Sort sort);

    List<Document> findByIsHoldAndIsDeletedAndDateTimeBefore(boolean isHold, boolean isDeleted, LocalDateTime dateTime, Sort sort);

    List<Document> findByIsHoldAndDateTimeBetween(boolean isHold, LocalDateTime fromTime, LocalDateTime toTime, Sort sort);

    boolean existsByDateTime(LocalDateTime dateTime);

    Optional<Document> getFirstByDateTimeBetweenOrderByDateTimeDesc(LocalDateTime start, LocalDateTime end);

    boolean existsByNumberAndDocTypeAndDateTimeAfter(long number, DocumentType docType, LocalDateTime dateTime);

    int deleteByIsDeleted(boolean isDeleted);
}
