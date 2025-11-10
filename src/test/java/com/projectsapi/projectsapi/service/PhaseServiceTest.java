package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.Phase;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.PhaseStatus;
import com.projectsapi.projectsapi.repository.PhaseRepository;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.PhaseStatusRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PhaseServiceTest {

    @Mock
    private PhaseRepository phaseRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private PhaseStatusRepository phaseStatusRepository;

    @InjectMocks
    private PhaseService phaseService;

    private Phase phase;
    private Project project;
    private PhaseStatus status;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        project = new Project();
        project.setIDProject(1);
        project.setName("Proyecto Demo");

        status = new PhaseStatus();
        status.setIDPhaseStatus(1);
        status.setName("En progreso");

        phase = new Phase();
        phase.setIDPhase(1);
        phase.setName("Fase 1");
        phase.setDescription("Descripción de prueba");
        phase.setStartDate(LocalDate.of(2025, 1, 1));
        phase.setEndDate(LocalDate.of(2025, 2, 1));
        phase.setPercentageProgress(50);
        phase.setBudget(1000);
        phase.setCost(500);
        phase.setIDProjectRef(1);
        phase.setIDPhaseStatusRef(1);
    }

    // =====================================================
    // GET ALL PHASES
    // =====================================================
    @Test
    void testGetAllPhases() {
        when(phaseRepository.findAll()).thenReturn(List.of(phase));

        List<Phase> result = phaseService.getAllPhases();

        assertEquals(1, result.size());
        assertEquals("Fase 1", result.get(0).getName());
        verify(phaseRepository, times(1)).findAll();
    }

    // =====================================================
    // GET PHASE BY ID
    // =====================================================
    @Test
    void testGetPhaseById() {
        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));

        Optional<Phase> result = phaseService.getPhaseById(1);

        assertTrue(result.isPresent());
        assertEquals("Fase 1", result.get().getName());
        verify(phaseRepository, times(1)).findById(1);
    }

    // =====================================================
    // CREATE PHASE
    // =====================================================
    // @Test
    // void testCreatePhase() {
    //     when(projectRepository.findById(1)).thenReturn(Optional.of(project));
    //     when(phaseStatusRepository.findById(1)).thenReturn(Optional.of(status));
    //     when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

    //     Phase created = phaseService.createPhase(phase);

    //     assertNotNull(created);
    //     assertEquals("Fase 1", created.getName());
    //     verify(phaseRepository, times(1)).save(any(Phase.class));
    // }

    @Test
    void testCreatePhaseThrowsErrorWhenProjectMissing() {
        phase.setIDProjectRef(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.createPhase(phase));

        assertEquals("Project ID is required", exception.getMessage());
    }

    // =====================================================
    // UPDATE PHASE
    // =====================================================
    @Test
    void testUpdatePhase() {
        Phase updated = new Phase();
        updated.setName("Fase Actualizada");
        updated.setDescription("Nueva descripción");

        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(updated);

        Phase result = phaseService.updatePhase(1, updated);

        assertEquals("Fase Actualizada", result.getName());
        verify(phaseRepository, times(1)).save(any(Phase.class));
    }

    @Test
    void testUpdatePhaseNotFound() {
        when(phaseRepository.findById(99)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.updatePhase(99, phase));

        assertEquals("Phase not found", exception.getMessage());
    }

}
