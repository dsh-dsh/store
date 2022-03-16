package com.example.sklad.repositories;

import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDocRepository extends JpaRepository<OrderDoc, Integer> {
    List<OrderDoc> findByDocType(DocumentType documentType);
}
