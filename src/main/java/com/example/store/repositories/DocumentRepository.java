package com.example.store.repositories;

import com.example.store.model.entities.Company;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    boolean existsByDateTime(LocalDateTime dateTime);

    @Query(value = "select * from document where author_id not in :authorIds and date_time between :from and :to order by date_time limit 1", nativeQuery = true)
    Optional<Document> getFirstNotCheckDoc(List<Integer> authorIds, LocalDateTime from, LocalDateTime to);

    @Query(value = "select * from document where author_id not in :authorIds and date_time between :from and :to order by date_time desc limit 1", nativeQuery = true)
    Optional<Document> getLastNotCheckDoc(List<Integer> authorIds, LocalDateTime from, LocalDateTime to);

    Optional<Document> getFirstByDateTimeBetween(LocalDateTime from, LocalDateTime to, Sort sort);

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
    List<Document> findDocsToPayment(DocumentType docType, Company supplier, boolean isPayed, Sort sort);

    @Query(value = "select document.id from document " +
            "where is_deleted = false and base_document_id = :docId", nativeQuery = true)
    List<Integer> getRelativeDocIds(int docId);

    @Transactional
    @Modifying
    @Query(value = "update document set date_time = :time " +
            "where doc_type = :type", nativeQuery = true)
    void updateDateTimeOfDocForTestsOnly(String type, LocalDateTime time);

    // method for SerialUnHoldDocService
    @Query(value = "select * from document " +
            "where author_id in :authorIds " +
            "and date_time between :from and :to order by date_time limit 1", nativeQuery = true)
    Optional<Document> findFistCheckDocOfDay(List<Integer> authorIds, LocalDateTime from, LocalDateTime to);

    // method for SerialUnHoldDocService
    @Modifying
    @Query(value = "update document set is_hold = :isHold where date_time >= :from", nativeQuery = true)
    void setIsHold(boolean isHold, LocalDateTime from);

    // method for SerialUnHoldDocService
    @Modifying
    @Query(value = "update document set is_deleted = 1 " +
            "where author_id = :authorId and date_time >= :from", nativeQuery = true)
    void softDeleteDocs(int authorId, LocalDateTime from);

    @Query(value = "select * from document " +
            "where author_id in :authorIds and doc_type != 'PERIOD_REST_MOVE_DOC' " +
            "and date_time between :from and :to order by date_time desc limit 1", nativeQuery = true)
    Optional<Document> findLastCheckDocOfDay(List<Integer> authorIds, LocalDateTime from, LocalDateTime to);
}