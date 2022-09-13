package com.example.store.repositories;

import com.example.store.model.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByLastNameStartingWithIgnoreCase(String email);

    Optional<User> findByCode(int code);

    List<User> findByEmailNotLike(String email, Sort sort);

    List<User> findByParent(User item);

    @Transactional
    @Modifying
    @Query(value = "update users set parent_id = 0 where id = :userId", nativeQuery = true)
    void setParentIdNotNull(int userId);

    boolean existsByCode(int code);

}
