package com.example.sklad.repositories;

import com.example.sklad.model.entities.CheckInfo;
import com.example.sklad.model.entities.documents.ItemDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckInfoRepository extends JpaRepository<CheckInfo, Integer> {

    Optional<CheckInfo> findByCheck(ItemDoc check);

    @Query("DELETE CheckInfo AS checkInfo " +
            "WHERE checkInfo.check = : check")
    void deleteByCheck(ItemDoc check);

}
