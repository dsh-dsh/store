package com.example.sklad.repositories;

import com.example.sklad.model.entities.DocumentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocItemRepository extends JpaRepository<DocumentItem, Long> {

}
