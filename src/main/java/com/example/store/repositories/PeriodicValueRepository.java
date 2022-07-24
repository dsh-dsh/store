package com.example.store.repositories;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodicValueRepository extends JpaRepository<PeriodicValue, Integer> {

    List<PeriodicValue> findByIngredientAndDateLessThanEqual(Ingredient ingredient, LocalDate date, Sort sort);

    Optional<PeriodicValue> findFirstByIngredientAndDateAndType(Ingredient ingredient, LocalDate date, PeriodicValueType type);

    Optional<PeriodicValue> findFirstByIngredientAndDateLessThanEqualAndType(Ingredient ingredient, LocalDate date, PeriodicValueType type, Sort sort);

}
