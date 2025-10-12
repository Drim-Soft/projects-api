package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.ProjectStatus;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;
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
        // Log de depuración (puedes imprimir para verificar)
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

        return projectRepository.save(project);
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
