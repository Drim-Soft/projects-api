package com.projectsapi.projectsapi.controller;

import com.projectsapi.projectsapi.model.ProjectStatus;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;
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

@WebMvcTest(ProjectStatusController.class)
class ProjectStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectStatusRepository projectStatusRepository;

    private ProjectStatus testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new ProjectStatus();
        testStatus.setIDProjectStatus(1);
        testStatus.setName("En Progreso");
    }

    @Test
    void testGetAllStatuses() throws Exception {
        when(projectStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));

        mockMvc.perform(get("/project-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("En Progreso"));

        verify(projectStatusRepository).findAll();
    }
}

