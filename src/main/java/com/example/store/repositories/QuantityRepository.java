package com.example.store.repositories;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import com.example.store.model.enums.QuantityType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuantityRepository extends JpaRepository<Quantity, Integer> {

    List<Quantity> findByIngredientAndDateLessThanEqual(Ingredient ingredient, LocalDate date, Sort sort);

    Optional<Quantity> findTop1ByTypeAndDateLessThanEqualOrderByDateDesc(QuantityType type, LocalDate date);

}
