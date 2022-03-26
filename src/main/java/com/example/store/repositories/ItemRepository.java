package com.example.store.repositories;

import com.example.store.model.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(value = "select parent_id from item where id = :childId", nativeQuery = true)
    int getParentId(@Param("childId") int childId);

    @Query(value = "select set_id from sets where item_id = :itemId", nativeQuery = true)
    List<Integer> getDinnerIdList(int itemId);

    Optional<Item> findByName(String name);

    Item getByName(String name);

}
