package com.example.sklad.repositories;

import com.example.sklad.model.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query(value = "select parent_id from item where id = :childId", nativeQuery = true)
    int getParentId(int childId);

    @Query(value = "select set_id from sets where item_id = :itemId", nativeQuery = true)
    List<Integer> getDinnerIdList(int itemId);

}
