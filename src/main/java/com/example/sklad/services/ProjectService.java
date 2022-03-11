package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.entities.Project;
import com.example.sklad.repositories.ProjectRepository;
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
