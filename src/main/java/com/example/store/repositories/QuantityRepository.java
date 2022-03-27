package com.example.store.repositories;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Quantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuantityRepository extends JpaRepository<Quantity, Integer> {

    List<Quantity> findByIngredientAndDateLessThanEqual(Ingredient ingredient, LocalDate date);

}
