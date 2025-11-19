package com.projectsapi.projectsapi.controller;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ProjectRepository projectRepository;

    private Project testProject;
    private Methodology testMethodology;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Ágil");

        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setMethodology(testMethodology);

        testRole = new Role();
        testRole.setIDRole(1);
        testRole.setName("Administrador");
    }

    @Test
    void testGetRolesByProject_Success() throws Exception {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(Arrays.asList(testRole));

        mockMvc.perform(get("/projects/1/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Administrador"));

        verify(projectRepository).findById(1);
        verify(roleRepository).findByMethodology_IDMethodology(1);
    }

    @Test
    void testGetRolesByProject_ProjectNotFound() throws Exception {
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/999/roles"))
                .andExpect(status().isBadRequest());

        verify(projectRepository).findById(999);
    }

    @Test
    void testGetRolesByProject_NoMethodology() throws Exception {
        testProject.setMethodology(null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        mockMvc.perform(get("/projects/1/roles"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Project has no methodology assigned"));

        verify(projectRepository).findById(1);
        verify(roleRepository, never()).findByMethodology_IDMethodology(anyInt());
    }
}

