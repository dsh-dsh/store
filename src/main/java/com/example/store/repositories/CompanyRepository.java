package com.example.store.repositories;

import com.example.store.model.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

    Optional<Company> findByNameIgnoreCase(String name);
    Optional<Company> findByInn(long inn);
    Optional<Company> findByCode(int code);
    List<Company> findByParent(Company company);

    @Transactional
    @Modifying
    @Query(value = "update company set parent_id = 0 where id = :companyId", nativeQuery = true)
    void setParentIdNotNull(int companyId);
}
