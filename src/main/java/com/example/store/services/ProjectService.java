package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.ProjectDTO;
import com.example.store.model.entities.Project;
import com.example.store.repositories.ProjectRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Project getByName(String name) {
        return projectRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(String.format(Constants.NO_SUCH_PROJECT_MESSAGE, name)));
    }

    public Project getById(int id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_PROJECT_MESSAGE));
    }

    public List<ProjectDTO> getProjectDTOList() {
        return getProjectList().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<Project> getProjectList() {
        return projectRepository.findAll();
    }

    public ProjectDTO mapToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setCode(String.valueOf(project.getId()));
        return dto;
    }

    public Optional<Project> getProjectByStorageName(String name) {
        return projectRepository.findByNameIgnoreCase(name);
    }

    public ProjectDTO getProjectDTOById(int projectId) {
        Project project = getById(projectId);
        return mapToDTO(project);
    }
}
