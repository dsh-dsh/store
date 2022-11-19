package com.example.store.repositories;

import com.example.store.model.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Integer> {

    Optional<Period> findByIsCurrent (boolean isCurrent);

    @Query(value = "select start_date from period where start_date <= :date and end_date >= :date", nativeQuery = true)
    Optional<LocalDate> findStartDateByDateInPeriod(LocalDate date);

}
