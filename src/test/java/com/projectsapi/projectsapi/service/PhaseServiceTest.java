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

    @Test
    void testGetAllActivePhases() {
        when(phaseRepository.findAllActive()).thenReturn(List.of(phase));

        List<Phase> result = phaseService.getAllActivePhases();

        assertEquals(1, result.size());
        assertEquals("Fase 1", result.get(0).getName());
        verify(phaseRepository, times(1)).findAllActive();
    }

    @Test
    void testGetPhasesByProject() {
        phase.setPhaseStatus(status);
        
        Phase phase2 = new Phase();
        phase2.setIDPhase(2);
        phase2.setPhaseStatus(status);

        Phase deletedPhase = new Phase();
        PhaseStatus deletedStatus = new PhaseStatus();
        deletedStatus.setIDPhaseStatus(3);
        deletedPhase.setIDPhase(3);
        deletedPhase.setPhaseStatus(deletedStatus);

        when(phaseRepository.findByProject_IDProject(1)).thenReturn(List.of(phase, phase2, deletedPhase));

        List<Phase> result = phaseService.getPhasesByProject(1);

        assertEquals(2, result.size());
        verify(phaseRepository, times(1)).findByProject_IDProject(1);
    }

    @Test
    void testCreatePhase_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(phaseStatusRepository.findById(1)).thenReturn(Optional.of(status));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        Phase created = phaseService.createPhase(phase);

        assertNotNull(created);
        assertEquals("Fase 1", created.getName());
        verify(phaseRepository, times(1)).save(any(Phase.class));
    }

    @Test
    void testCreatePhaseThrowsErrorWhenStatusMissing() {
        phase.setIDPhaseStatusRef(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.createPhase(phase));

        assertEquals("Phase Status ID is required", exception.getMessage());
    }

    @Test
    void testCreatePhase_ProjectNotFound() {
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        phase.setIDProjectRef(999);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.createPhase(phase));

        assertTrue(exception.getMessage().contains("Project not found"));
    }

    @Test
    void testCreatePhase_StatusNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(phaseStatusRepository.findById(999)).thenReturn(Optional.empty());

        phase.setIDPhaseStatusRef(999);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.createPhase(phase));

        assertTrue(exception.getMessage().contains("PhaseStatus not found"));
    }

    @Test
    void testUpdatePhase_WithRelations() {
        Phase updated = new Phase();
        updated.setName("Fase Actualizada");
        updated.setIDProjectRef(2);
        updated.setIDPhaseStatusRef(2);

        Project newProject = new Project();
        newProject.setIDProject(2);
        PhaseStatus newStatus = new PhaseStatus();
        newStatus.setIDPhaseStatus(2);

        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(projectRepository.findById(2)).thenReturn(Optional.of(newProject));
        when(phaseStatusRepository.findById(2)).thenReturn(Optional.of(newStatus));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        Phase result = phaseService.updatePhase(1, updated);

        assertNotNull(result);
        verify(phaseRepository, times(1)).save(any(Phase.class));
    }

    @Test
    void testDeletePhase_Success() {
        PhaseStatus deletedStatus = new PhaseStatus();
        deletedStatus.setIDPhaseStatus(3);
        deletedStatus.setName("Eliminada");

        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(phaseStatusRepository.findById(3)).thenReturn(Optional.of(deletedStatus));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        phaseService.deletePhase(1);

        verify(phaseRepository, times(1)).save(any(Phase.class));
        assertEquals(deletedStatus, phase.getPhaseStatus());
    }

    @Test
    void testDeletePhase_NotFound() {
        when(phaseRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.deletePhase(999));

        assertEquals("Phase not found", exception.getMessage());
    }

    @Test
    void testDeletePhase_DeletedStatusNotFound() {
        when(phaseRepository.findById(1)).thenReturn(Optional.of(phase));
        when(phaseStatusRepository.findById(3)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> phaseService.deletePhase(1));

        assertTrue(exception.getMessage().contains("Deleted status"));
    }

}
