package com.example.store.repositories;

import com.example.store.model.entities.DocInfo;
import com.example.store.model.entities.documents.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocInfoRepository extends JpaRepository<DocInfo, Integer> {
    Optional<DocInfo> findByDocument(Document document);

    void deleteByDocument(Document check);
}
