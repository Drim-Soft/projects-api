package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.*;
import com.projectsapi.projectsapi.repository.PhaseRepository;
import com.projectsapi.projectsapi.repository.PhaseStatusRepository;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.service.PhaseService;
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

@WebMvcTest(PhaseController.class)
class PhaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhaseService phaseService;

    @MockBean
    private PhaseRepository phaseRepository;

    @MockBean
    private PhaseStatusRepository phaseStatusRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Phase testPhase;
    private Project testProject;
    private PhaseStatus testStatus;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setName("Proyecto Test");

        testStatus = new PhaseStatus();
        testStatus.setIDPhaseStatus(1);
        testStatus.setName("En Progreso");

        testPhase = new Phase();
        testPhase.setIDPhase(1);
        testPhase.setName("Fase Test");
        testPhase.setDescription("Descripción");
        testPhase.setIDProjectRef(1);
        testPhase.setIDPhaseStatusRef(1);
    }

    @Test
    void testGetAllPhases() throws Exception {
        when(phaseService.getAllPhases()).thenReturn(Arrays.asList(testPhase));

        mockMvc.perform(get("/phases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fase Test"));

        verify(phaseService).getAllPhases();
    }

    @Test
    void testGetAllActivePhases() throws Exception {
        when(phaseService.getAllActivePhases()).thenReturn(Arrays.asList(testPhase));

        mockMvc.perform(get("/phases/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fase Test"));

        verify(phaseService).getAllActivePhases();
    }

    @Test
    void testGetPhaseById_Success() throws Exception {
        when(phaseService.getPhaseById(1)).thenReturn(Optional.of(testPhase));

        mockMvc.perform(get("/phases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fase Test"));

        verify(phaseService).getPhaseById(1);
    }

    @Test
    void testGetPhaseById_NotFound() throws Exception {
        when(phaseService.getPhaseById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/phases/999"))
                .andExpect(status().isNotFound());

        verify(phaseService).getPhaseById(999);
    }

    @Test
    void testGetPhasesByProject() throws Exception {
        when(phaseService.getPhasesByProject(1)).thenReturn(Arrays.asList(testPhase));

        mockMvc.perform(get("/phases/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fase Test"));

        verify(phaseService).getPhasesByProject(1);
    }

    @Test
    void testCreatePhase_Success() throws Exception {
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(phaseStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(phaseRepository.save(any(Phase.class))).thenReturn(testPhase);

        mockMvc.perform(post("/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPhase)))
                .andExpect(status().isOk());

        verify(phaseRepository).save(any(Phase.class));
    }

    @Test
    void testCreatePhase_MissingProjectId() throws Exception {
        testPhase.setIDProjectRef(null);

        mockMvc.perform(post("/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPhase)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Project ID is required"));
    }

    @Test
    void testCreatePhase_ProjectNotFound() throws Exception {
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        testPhase.setIDProjectRef(999);

        mockMvc.perform(post("/phases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPhase)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePhase_Success() throws Exception {
        when(phaseService.updatePhase(eq(1), any(Phase.class))).thenReturn(testPhase);

        mockMvc.perform(put("/phases/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPhase)))
                .andExpect(status().isOk());

        verify(phaseService).updatePhase(eq(1), any(Phase.class));
    }

    @Test
    void testUpdatePhase_NotFound() throws Exception {
        when(phaseService.updatePhase(eq(999), any(Phase.class)))
                .thenThrow(new IllegalArgumentException("Phase not found"));

        mockMvc.perform(put("/phases/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPhase)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeletePhase_Success() throws Exception {
        doNothing().when(phaseService).deletePhase(1);

        mockMvc.perform(delete("/phases/1"))
                .andExpect(status().isOk());

        verify(phaseService).deletePhase(1);
    }

    @Test
    void testDeletePhase_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Phase not found")).when(phaseService).deletePhase(999);

        mockMvc.perform(delete("/phases/999"))
                .andExpect(status().isBadRequest());
    }
}

