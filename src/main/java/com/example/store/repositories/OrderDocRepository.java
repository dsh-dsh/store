package com.example.store.repositories;

import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDocRepository extends JpaRepository<OrderDoc, Integer> {
    List<OrderDoc> findByDocType(DocumentType documentType);
}
