package com.example.store.repositories;

import com.example.store.model.projections.ItemDTOForListInterface;
import com.example.store.model.entities.Item;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    boolean existsByNumber(int number);

    Item getByName(String name);

    Optional<Item> findByNumber(int number);

    @Query(value = "select parent_id from item where id = :childId", nativeQuery = true)
    int getParentId(@Param("childId") int childId);

    @Query(value = "select id from item where number = :number", nativeQuery = true)
    int getItemIdByNumber(int number);

    @Query(value = "select i.id, i.name, i.parent_id as parentId from item as i", nativeQuery = true)
    List<ItemDTOForListInterface> getItemDTOList();

    List<Item> findAll(Sort sort);

}
