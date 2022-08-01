package com.example.store.repositories;

import com.example.store.model.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Integer> {
    Optional<Period> findByIsCurrent (boolean isCurrent);
}
