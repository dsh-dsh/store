package com.example.store.repositories;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.ItemSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetRepository extends JpaRepository<ItemSet, Integer> {
    List<ItemSet> findSetsByItem(Item item);
}
