package com.example.store.repositories;

import com.example.store.model.entities.Item;
import com.example.store.model.entities.Price;
import com.example.store.model.enums.PriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {

    List<Price> findByItem(Item item);

    Optional<Price> findFirstByItemAndPriceTypeAndDateLessThanEqualOrderByDateDesc(Item item, PriceType priceType, LocalDate date);

    List<Price> findByItemAndDateLessThan(Item item, LocalDate date);

}