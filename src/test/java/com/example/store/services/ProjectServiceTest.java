package com.example.store.services;

import com.example.store.model.dto.ProjectDTO;
import com.example.store.model.entities.Project;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    public static final String PROJECT_NAME = "Жаровня 3";
    public static final int PROJECT_ID = 4;

    @Test
    void getByNameTest() {
        Project project = projectService.getByName(PROJECT_NAME);
        assertEquals(PROJECT_ID, project.getId());
    }

    @Test
    void getByIdTest() {
        Project project = projectService.getById(PROJECT_ID);
        assertEquals(PROJECT_NAME, project.getName());
    }

    @Test
    void getProjectDTOListTest() {
        List<ProjectDTO> list = projectService.getProjectDTOList();
        assertEquals(4, list.size());
        assertEquals(PROJECT_NAME, list.get(3).getName());
    }

    @Test
    void getProjectListTest() {
        List<Project> list = projectService.getProjectList();
        assertEquals(4, list.size());
    }

    @Test
    void mapToDTOTest() {
        Project project = projectService.getById(PROJECT_ID);
        ProjectDTO dto = projectService.mapToDTO(project);
        assertEquals(PROJECT_NAME, dto.getName());
    }
}