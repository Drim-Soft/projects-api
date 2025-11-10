package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.projectsapi.projectsapi.model.*;
import com.projectsapi.projectsapi.repository.*;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private MethodologyRepository methodologyRepository;
    @Autowired private ProjectStatusRepository projectStatusRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRoleProjectRepository userRoleProjectRepository;

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
        if (project.getIDMethodologyRef() == null)
            throw new IllegalArgumentException("Methodology ID is required");
        if (project.getIDProjectStatusRef() == null)
            throw new IllegalArgumentException("ProjectStatus ID is required");

        Methodology methodology = methodologyRepository.findById(project.getIDMethodologyRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Methodology ID"));
        ProjectStatus status = projectStatusRepository.findById(project.getIDProjectStatusRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid ProjectStatus ID"));

        project.setMethodology(methodology);
        project.setProjectStatus(status);

        Project savedProject = projectRepository.save(project);

        // =====================================================
        // Asignar automáticamente el rol “Administrador Proyecto” al creador
        // =====================================================
        try {
            Integer creatorId = 1; // temporal mientras el front no envía usuario real

            Role adminRole = roleRepository.findByMethodology_IDMethodology(methodology.getIDMethodology())
                    .stream()
                    .filter(r -> r.getName().equalsIgnoreCase("Administrador Proyecto"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el rol 'Administrador Proyecto' para la metodología con ID "
                        + methodology.getIDMethodology()
                    ));

            userRoleProjectRepository.assignUserToProject(
                    creatorId, adminRole.getIDRole(), savedProject.getIDProject()
            );

            System.out.println("✅ Rol 'Administrador Proyecto' asignado correctamente (metodología "
                    + methodology.getIDMethodology() + ")");

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
                .filter(ps -> ps.getName().equalsIgnoreCase("Deleted") || ps.getName().equalsIgnoreCase("Eliminado"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Deleted status not found"));
        project.setProjectStatus(deletedStatus);
        projectRepository.save(project);
    }

    // =====================================================
    // GET PROJECTS BY USER ID
    // =====================================================
    public List<Project> getProjectsByUserId(Integer userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID cannot be null");
        return projectRepository.findProjectsByUserId(userId);
    }

    // =====================================================
    // ASSIGN USER TO PROJECT
    // =====================================================
    @Transactional
    public void assignUserToProject(Integer userId, Integer projectId, String roleName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Integer methodologyId = project.getMethodology().getIDMethodology();

        Role role = roleRepository.findByMethodology_IDMethodology(methodologyId)
                .stream()
                .filter(r -> r.getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "Role not found: " + roleName + " para la metodología ID " + methodologyId
                ));

        // ✅ Verificar si ya existe antes de insertar
        boolean alreadyExists = userRoleProjectRepository.findByIDProject(projectId).stream()
                .anyMatch(urp -> urp.getIDUser().equals(userId) && urp.getIDRole().equals(role.getIDRole()));

        if (!alreadyExists) {
            userRoleProjectRepository.assignUserToProject(userId, role.getIDRole(), projectId);
            System.out.println("✅ Asignación registrada: user=" + userId + ", role=" + role.getIDRole() + ", project=" + projectId);
        } else {
            System.out.println("⚠️ Ya existía esa asignación, no se volvió a insertar.");
        }
    }

}