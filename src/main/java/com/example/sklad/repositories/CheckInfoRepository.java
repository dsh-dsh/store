package com.example.sklad.repositories;

import com.example.sklad.model.entities.CheckKKMInfo;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckInfoRepository extends JpaRepository<CheckKKMInfo, Integer> {

    Optional<CheckKKMInfo> findByCheck(ItemDoc check);

}
