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

    void deleteByCheck(ItemDoc check);

    @Query(value = "select count(*) from check_kkm_info where check_id = :checkId", nativeQuery = true)
    int countRowsByDocId(int checkId);

}
