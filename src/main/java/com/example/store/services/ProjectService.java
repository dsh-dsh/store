package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Project;
import com.example.store.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project getByName(String name) {
        return projectRepository.findByNameIgnoreCase(name)
                .orElseThrow(BadRequestException::new);
    }

    public Project getById(int id) {
        return projectRepository.findById(id)
                .orElseThrow(BadRequestException::new);
    }

}
