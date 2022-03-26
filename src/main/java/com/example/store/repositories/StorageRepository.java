package com.example.store.repositories;

import com.example.store.model.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageRepository extends JpaRepository<Storage, Integer> {

    Optional<Storage> findByNameIgnoreCase(String name);

}
