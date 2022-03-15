package com.example.sklad.repositories;

import com.example.sklad.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailIgnoreCase(String email);

}
