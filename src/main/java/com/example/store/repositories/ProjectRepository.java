package com.example.store.repositories;

import com.example.store.model.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {

    Optional<Project> findByNameIgnoreCase(String name);

}
