package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.ProjectStatus;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MethodologyRepository methodologyRepository;

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectUserService projectUserService;

    // =====================================================
    // GET ALL PROJECTS
    // =====================================================
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    // =====================================================
    // GET PROJECT BY ID
    // =====================================================
    public Optional<Project> getProjectById(Integer id) {
        return projectRepository.findById(id);
    }

    // =====================================================
    // CREATE PROJECT
    // =====================================================
    public Project createProject(Project project) {
        System.out.println("📩 Recibido: MethodologyRef=" + project.getIDMethodologyRef() +
                        " | ProjectStatusRef=" + project.getIDProjectStatusRef());

        if (project.getIDMethodologyRef() == null) {
            throw new IllegalArgumentException("Methodology ID is required");
        }
        if (project.getIDProjectStatusRef() == null) {
            throw new IllegalArgumentException("ProjectStatus ID is required");
        }

        Methodology methodology = methodologyRepository.findById(project.getIDMethodologyRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Methodology ID"));
        ProjectStatus status = projectStatusRepository.findById(project.getIDProjectStatusRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid ProjectStatus ID"));

        project.setMethodology(methodology);
        project.setProjectStatus(status);

        // Guardar el proyecto
        Project savedProject = projectRepository.save(project);

        // =====================================================
        // Asignar automáticamente el rol “Administrador Proyecto” al creador
        // =====================================================
        try {
            Integer creatorId = project.getIDProjectStatusRef(); 
            // ⚠️ Este valor NO viene de BD. Debes enviarlo en el body del POST, 
            // por ejemplo: { "IDUser": 5, "IDMethodologyRef": 1, "IDProjectStatusRef": 1, ... }
            // Es responsabilidad del front incluir el IDUser creador.

            if (creatorId == null) {
                System.out.println("⚠️ No se recibió IDUser en el request, no se puede asignar rol automáticamente.");
                return savedProject;
            }

            Role adminRole = roleRepository.findByMethodology_IDMethodology(methodology.getIDMethodology())
                    .stream()
                    .filter(r -> r.getName().equalsIgnoreCase("Administrador Proyecto"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Role 'Administrador Proyecto' not found for this methodology"));

            projectUserService.insertRelation(creatorId, adminRole.getIDRole(), savedProject.getIDProject());
            System.out.println("✅ Asignado automáticamente rol 'Administrador Proyecto' al usuario " + creatorId);

        } catch (Exception e) {
            System.out.println("⚠️ Error al asignar rol automáticamente: " + e.getMessage());
        }

        return savedProject;
    }

    // =====================================================
    // UPDATE PROJECT
    // =====================================================
    public Project updateProject(Integer id, Project updatedProject) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setStartDate(updatedProject.getStartDate());
        existingProject.setEndDate(updatedProject.getEndDate());
        existingProject.setBudget(updatedProject.getBudget());
        existingProject.setCost(updatedProject.getCost());
        existingProject.setPercentageProgress(updatedProject.getPercentageProgress());
        existingProject.setPercentageBudgetExecution(updatedProject.getPercentageBudgetExecution());

        if (updatedProject.getIDMethodologyRef() != null) {
            Methodology methodology = methodologyRepository.findById(updatedProject.getIDMethodologyRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Methodology ID"));
            existingProject.setMethodology(methodology);
        }

        if (updatedProject.getIDProjectStatusRef() != null) {
            ProjectStatus status = projectStatusRepository.findById(updatedProject.getIDProjectStatusRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid ProjectStatus ID"));
            existingProject.setProjectStatus(status);
        }

        return projectRepository.save(existingProject);
    }

    // =====================================================
    // DELETE PROJECT (BORRADO LÓGICO)
    // =====================================================
    public void deleteProject(Integer id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        ProjectStatus deletedStatus = projectStatusRepository.findAll().stream()
                .filter(ps -> ps.getName().equalsIgnoreCase("Deleted") ||
                              ps.getName().equalsIgnoreCase("Eliminado"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Deleted status not found"));

        project.setProjectStatus(deletedStatus);
        projectRepository.save(project);
    }
}
