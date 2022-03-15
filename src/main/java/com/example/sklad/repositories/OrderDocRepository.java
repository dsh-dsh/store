package com.example.sklad.repositories;

import com.example.sklad.model.entities.documents.OrderDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDocRepository extends JpaRepository<OrderDoc, Integer> {
}
