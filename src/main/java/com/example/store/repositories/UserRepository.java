package com.example.store.repositories;

import com.example.store.model.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByLastNameStartingWithIgnoreCase(String email);

    Optional<User> findByCode(int code);

    List<User> findByEmailNotLike(String email, Sort sort);

}
