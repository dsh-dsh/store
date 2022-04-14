package com.example.store.repositories;

import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDocRepository extends JpaRepository<OrderDoc, Integer> {
    List<OrderDoc> findByDocType(DocumentType documentType);

    List<OrderDoc> findByDocTypeInAndProjectAndIsHoldAndDateTimeBetween(
            List<DocumentType> docTypes, Project project, boolean isHold,
            LocalDateTime from, LocalDateTime to);
}
