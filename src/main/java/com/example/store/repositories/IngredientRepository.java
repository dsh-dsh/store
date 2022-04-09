package com.example.store.repositories;

import com.example.store.model.entities.Ingredient;
import com.example.store.model.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    List<Ingredient> findByParentAndIsDeleted(Item parent, boolean isDeleted);

}
