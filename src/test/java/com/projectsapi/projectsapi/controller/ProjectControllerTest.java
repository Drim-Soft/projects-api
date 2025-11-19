package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.*;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;
import com.projectsapi.projectsapi.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private MethodologyRepository methodologyRepository;

    @MockBean
    private ProjectStatusRepository projectStatusRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Project testProject;
    private Methodology testMethodology;
    private ProjectStatus testStatus;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Ágil");

        testStatus = new ProjectStatus();
        testStatus.setIDProjectStatus(1);
        testStatus.setName("En Progreso");

        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setName("Proyecto Test");
        testProject.setDescription("Descripción");
        testProject.setIDMethodologyRef(1);
        testProject.setIDProjectStatusRef(1);
    }

    @Test
    void testGetAllProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(testProject));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Proyecto Test"));

        verify(projectService).getAllProjects();
    }

    @Test
    void testGetProjectById_Success() throws Exception {
        when(projectService.getProjectById(1)).thenReturn(Optional.of(testProject));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Proyecto Test"));

        verify(projectService).getProjectById(1);
    }

    @Test
    void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/999"))
                .andExpect(status().isNotFound());

        verify(projectService).getProjectById(999);
    }

    @Test
    void testCreateProject_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Nuevo Proyecto");
        body.put("description", "Descripción");
        body.put("methodologyName", "Ágil");
        body.put("statusName", "En Progreso");
        body.put("userId", 1);
        body.put("startDate", "2025-01-01");
        body.put("endDate", "2025-12-31");

        when(methodologyRepository.findByNameIgnoreCase("Ágil")).thenReturn(Optional.of(testMethodology));
        when(projectStatusRepository.findByNameIgnoreCase("En Progreso")).thenReturn(Optional.of(testStatus));
        when(projectService.createProject(any(Project.class))).thenReturn(testProject);
        doNothing().when(projectService).assignUserToProject(anyInt(), anyInt(), anyString());

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectService).createProject(any(Project.class));
    }

    @Test
    void testCreateProject_InvalidMethodology() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Nuevo Proyecto");
        body.put("methodologyName", "Inexistente");

        when(methodologyRepository.findByNameIgnoreCase("Inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).createProject(any());
    }

    @Test
    void testUpdateProject_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Proyecto Actualizado");
        body.put("budget", "200000");
        body.put("statusName", "En Progreso");

        when(projectStatusRepository.findByNameIgnoreCase("En Progreso")).thenReturn(Optional.of(testStatus));
        when(projectService.updateProject(eq(1), any(Project.class))).thenReturn(testProject);

        mockMvc.perform(put("/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectService).updateProject(eq(1), any(Project.class));
    }

    @Test
    void testUpdateProject_NotFound() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Proyecto Actualizado");

        when(projectService.updateProject(eq(999), any(Project.class)))
                .thenThrow(new IllegalArgumentException("Project not found"));

        mockMvc.perform(put("/projects/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1);

        mockMvc.perform(delete("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Project logically deleted"));

        verify(projectService).deleteProject(1);
    }

    @Test
    void testDeleteProject_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Project not found")).when(projectService).deleteProject(999);

        mockMvc.perform(delete("/projects/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetProjectsByUserId_Success() throws Exception {
        when(projectService.getProjectsByUserId(1)).thenReturn(Arrays.asList(testProject));

        mockMvc.perform(get("/projects/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Proyecto Test"));

        verify(projectService).getProjectsByUserId(1);
    }

    @Test
    void testGetProjectsByUserId_Empty() throws Exception {
        when(projectService.getProjectsByUserId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/projects/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testGetProjectsByUserId_InvalidUserId() throws Exception {
        when(projectService.getProjectsByUserId(null))
                .thenThrow(new IllegalArgumentException("User ID cannot be null"));

        mockMvc.perform(get("/projects/user/null"))
                .andExpect(status().isBadRequest());
    }
}

