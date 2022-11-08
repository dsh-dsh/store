package com.example.store.repositories;

import com.example.store.model.entities.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Integer> {

    Optional<Storage> findByNameIgnoreCase(String name);

}
