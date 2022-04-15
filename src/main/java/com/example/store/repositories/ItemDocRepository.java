package com.example.store.repositories;

import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ItemDocRepository extends JpaRepository<ItemDoc, Integer> {

    Optional<ItemDoc> findByNumber(int number);

    List<ItemDoc> findByDocType(DocumentType docType);

    List<ItemDoc> findByDocTypeAndStorageFromAndIsHoldAndDateTimeBetween(
            DocumentType docType, Storage storageFrom, boolean isHold,
            LocalDateTime from, LocalDateTime to);

}
