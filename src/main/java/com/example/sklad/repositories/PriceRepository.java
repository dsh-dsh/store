package com.example.sklad.repositories;

import com.example.sklad.model.entities.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Integer> {
}