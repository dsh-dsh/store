package com.example.store.repositories;

import com.example.store.model.entities.User;
import com.example.store.model.projections.ItemDTOForListInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailIgnoreCase(String email);

}
