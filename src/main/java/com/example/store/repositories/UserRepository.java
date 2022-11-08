package com.example.store.repositories;

import com.example.store.model.entities.User;
import com.example.store.model.enums.Role;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByLastNameStartingWithIgnoreCase(String email);

    Optional<User> findByCode(int code);

    List<User> findByRoleNotLike(Role role, Sort sort);

    List<User> findByIsNodeAndRoleNotLike(boolean isNode, Role role, Sort sort);

    List<User> findByParent(User item);

    @Transactional
    @Modifying
    @Query(value = "update users set parent_id = 0 where id = :userId", nativeQuery = true)
    void setParentIdNotNull(int userId);

    boolean existsByCode(int code);

    @Query(value = "select parent_id from users where id = :userId", nativeQuery = true)
    int getParentId(int userId);

}
