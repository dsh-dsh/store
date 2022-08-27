package com.example.store.repositories;

import com.example.store.model.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findByNameIgnoreCase(String name);
    Optional<Company> findByInn(long inn);
}
