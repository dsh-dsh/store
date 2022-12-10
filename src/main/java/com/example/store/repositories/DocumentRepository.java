package com.example.store.repositories;

import com.example.store.model.entities.Company;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.User;
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

    boolean existsByDateTimeBeforeAndIsDeletedAndIsHold(
            LocalDateTime dateTime, boolean isDeleted, boolean isHold);

    boolean existsByDateTimeAfterAndIsHold(LocalDateTime dateTime, boolean isHold);

    @Query("SELECT doc " +
            "FROM Document doc " +
            "WHERE (:filter = '' OR doc.docType IN (:types)) " +
            "AND doc.dateTime BETWEEN :start AND :end")
    List<Document> findByDocInFilter(
            String filter, Collection<DocumentType> types, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT number " +
            "FROM document " +
            "WHERE doc_type = :docType " +
            "AND number < 1000000000 " + // not 1C doc
            "AND date_time >= :currentYearStart " +
            "ORDER BY number DESC " +
            "LIMIT 1"
            , nativeQuery = true)
    int getLastNumber(String docType, LocalDateTime currentYearStart);

    List<Document> findByIsHoldAndDateTimeAfter(
            boolean isHold, LocalDateTime dateTime, Sort sort);

    List<Document> findByIsHoldAndIsDeletedAndDateTimeBefore(
            boolean isHold, boolean isDeleted, LocalDateTime dateTime, Sort sort);

    List<Document> findByIsHoldAndIsDeletedAndDateTimeBetween(
            boolean isHold, boolean isDeleted, LocalDateTime fromTime, LocalDateTime toTime, Sort sort);

    List<Document> findByDocTypeAndIsHoldAndIsDeletedAndDateTimeBetween(
            DocumentType docType, boolean isHold, boolean isDeleted,
            LocalDateTime fromTime, LocalDateTime toTime, Sort sort);

    boolean existsByDateTime(LocalDateTime dateTime);

    Optional<Document> getFirstByDateTimeBetween(LocalDateTime start, LocalDateTime end, Sort sort);

    boolean existsByNumberAndDocTypeAndDateTimeAfter(long number, DocumentType docType, LocalDateTime dateTime);

    int deleteByIsDeleted(boolean isDeleted);

    Optional<Document> getFirstByDateTimeAfterAndDocTypeAndIsHoldAndIsDeleted(
            LocalDateTime dateTime, DocumentType docType,
            boolean isHold, boolean isDeleted, Sort sort);

    @Query(value = "SELECT * " +
            "FROM document " +
            "WHERE date_time > :dateTime " +
            "AND number >= :from AND number < :to " +
            "ORDER BY date_time DESC " +
            "LIMIT 1",
            nativeQuery = true)
    Optional<Document> getLast1CDocNumber(long from, long to, LocalDateTime dateTime);

    @Query(value =  "select base_document_id from document where id = :docId", nativeQuery = true)
    int getBaseDocumentId(int docId);

    List<Document> findByDocTypeInAndProjectAndDateTimeBetween(
            List<DocumentType> docTypes, Project project, LocalDateTime from, LocalDateTime to);

    Optional<Document> findFirstByAuthorInAndIsHold(List<User> authors, boolean isHold, Sort sort);

    @Query("FROM Document " +
            "WHERE docType = :docType " +
            "AND (:supplier is null OR supplier = :supplier) " +
            "AND isPayed = :isPayed")
    List<Document> findDocsTtoPayment(DocumentType docType, Company supplier, boolean isPayed, Sort sort);

    List<Document> findBySupplierAndIsPayed(Company company, boolean isPayed);

}