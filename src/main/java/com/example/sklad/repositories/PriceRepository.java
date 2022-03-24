package com.example.sklad.repositories;

import com.example.sklad.model.entities.Item;
import com.example.sklad.model.entities.Price;
import com.example.sklad.model.enums.PriceType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Integer> {

    List<Price> findByItem(Item item);

    List<Price> findByItemAndPriceType(Item item, PriceType priceType, Pageable pageable);

}