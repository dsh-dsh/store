package com.example.store.repositories;

import com.example.store.model.projections.ItemDTOForListInterface;
import com.example.store.model.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    boolean existsByNumber(int number);

    Item getByName(String name);

    Optional<Item> findByNumber(int number);

    Item getByNumber(int number);

    @Query(value = "select * from item where parent_id in (:ids) order by name", nativeQuery = true)
    List<Item> findByParentIds(List<Integer> ids);

    @Query(value = "select parent_id from item where id = :childId", nativeQuery = true)
    int getParentId(@Param("childId") int childId);

    @Query(value = "select id from item where number = :number", nativeQuery = true)
    int getItemIdByNumber(int number);

    @Query(value = "select i.id, i.name, i.parent_id as parentId from item as i", nativeQuery = true)
    List<ItemDTOForListInterface> getItemDTOList();

    List<Item> findByParent(Item item);

    @Transactional
    @Modifying
    @Query(value = "update item set parent_id = 0 where id = :itemId", nativeQuery = true)
    void setParentIdNotNull(int itemId);

}
