package com.example.sklad.repositories;

import com.example.sklad.model.entities.CheckKKMInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInfoRepository extends JpaRepository<CheckKKMInfo, Long> {
}
