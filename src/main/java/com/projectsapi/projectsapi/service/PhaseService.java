package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.Phase;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.PhaseStatus;
import com.projectsapi.projectsapi.repository.PhaseRepository;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.PhaseStatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PhaseService {

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PhaseStatusRepository phaseStatusRepository;

    // =====================================================
    // GET ALL PHASES
    // =====================================================
    public List<Phase> getAllPhases() {
        return phaseRepository.findAll();
    }
    // =====================================================
    // GET ALL ACTIVE PHASES
    // =====================================================
    public List<Phase> getAllActivePhases() {
        return phaseRepository.findAllActive();
    }

    // =====================================================
    // GET PHASE BY ID
    // =====================================================
    public Optional<Phase> getPhaseById(Integer id) {
        return phaseRepository.findById(id);
    }

    // =====================================================
    // GET PHASES BY PROJECT
    // =====================================================
    public List<Phase> getPhasesByProject(Integer IDProject) {
        return phaseRepository.findByProject_IDProject(IDProject)
                .stream()
                .filter(p -> p.getPhaseStatus() != null && p.getPhaseStatus().getIDPhaseStatus() != 3)
                .toList();
    }

    // =====================================================
    // CREATE PHASE
    // =====================================================
    public Phase createPhase(Phase phase) {

        // Verificar que los IDs transitorios existan
        if (phase.getIDProjectRef() == null) {
            throw new IllegalArgumentException("Project ID is required");
        }

        if (phase.getIDPhaseStatusRef() == null) {
            throw new IllegalArgumentException("Phase Status ID is required");
        }

        // Buscar las entidades reales
        Project project = projectRepository.findById(phase.getIDProjectRef())
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID " + phase.getIDProjectRef()));

        PhaseStatus phaseStatus = phaseStatusRepository.findById(phase.getIDPhaseStatusRef())
                .orElseThrow(() -> new IllegalArgumentException("PhaseStatus not found with ID " + phase.getIDPhaseStatusRef()));

        // Asignarlas al objeto Phase antes de guardar
        phase.setProject(project);
        phase.setPhaseStatus(phaseStatus);

        return phaseRepository.save(phase);
    }

    // =====================================================
    // UPDATE PHASE
    // =====================================================
    public Phase updatePhase(Integer id, Phase updatedPhase) {
        Phase existingPhase = phaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phase not found"));

        // 🔹 Actualizar solo los campos presentes
        if (updatedPhase.getName() != null)
            existingPhase.setName(updatedPhase.getName());

        if (updatedPhase.getDescription() != null)
            existingPhase.setDescription(updatedPhase.getDescription());

        if (updatedPhase.getStartDate() != null)
            existingPhase.setStartDate(updatedPhase.getStartDate());

        if (updatedPhase.getEndDate() != null)
            existingPhase.setEndDate(updatedPhase.getEndDate());

        if (updatedPhase.getPercentageProgress() != null)
            existingPhase.setPercentageProgress(updatedPhase.getPercentageProgress());

        if (updatedPhase.getBudget() != null)
            existingPhase.setBudget(updatedPhase.getBudget());

        if (updatedPhase.getCost() != null)
            existingPhase.setCost(updatedPhase.getCost());

        // 🔹 Actualizar relaciones solo si vienen
        if (updatedPhase.getIDProjectRef() != null) {
            Project project = projectRepository.findById(updatedPhase.getIDProjectRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Project ID"));
            existingPhase.setProject(project);
        }

        if (updatedPhase.getIDPhaseStatusRef() != null) {
            PhaseStatus status = phaseStatusRepository.findById(updatedPhase.getIDPhaseStatusRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid PhaseStatus ID"));
            existingPhase.setPhaseStatus(status);
        }

        return phaseRepository.save(existingPhase);
    }


    // =====================================================
    // DELETE PHASE (BORRADO LÓGICO)
    // =====================================================
    public void deletePhase(Integer id) {
        Phase phase = phaseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phase not found"));

        PhaseStatus deletedStatus = phaseStatusRepository.findById(3)
                .orElseThrow(() -> new IllegalArgumentException("Deleted status (ID 3) not found"));

        phase.setPhaseStatus(deletedStatus);
        phaseRepository.save(phase);
    }
}
