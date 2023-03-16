package com.example.store.repositories;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.PeriodicValue;
import com.example.store.model.enums.PeriodicValueType;
import com.example.store.model.projections.IngredientQuantityProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PeriodicValueRepository extends JpaRepository<PeriodicValue, Integer> {

    List<PeriodicValue> findByIngredientAndDateLessThanEqual(Ingredient ingredient, LocalDate date, Sort sort);

    Optional<PeriodicValue> findFirstByIngredientAndDateAndType(Ingredient ingredient, LocalDate date, PeriodicValueType type);

    Optional<PeriodicValue> findFirstByIngredientAndDateLessThanEqualAndType(Ingredient ingredient, LocalDate date, PeriodicValueType type, Sort sort);

    List<PeriodicValue> findByIngredient(Ingredient ingredient);

    @Query(value = "" +
            "select ingredient_id as id, quantity " +
            "from periodic_quantity " +
            "where type = :type " +
            "and data <= :dateTime " +
            "order by ingredient_id asc, data desc", nativeQuery = true)
    List<IngredientQuantityProjection> getPeriodicQuantitiesOfType(String type, LocalDateTime dateTime);

}
