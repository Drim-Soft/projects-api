package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.service.ProjectService;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.ProjectStatusRepository;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/projects")
public class ProjectController {


    @Autowired
    private MethodologyRepository methodologyRepository;

    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Integer id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Map<String, Object> body) {
        try {

            Project project = new Project();
            project.setName((String) body.get("name"));
            project.setDescription((String) body.get("description"));

            //  Buscar metodología por nombre
            if (body.get("methodologyName") != null) {
                String methodologyName = (String) body.get("methodologyName");
                Integer idMethodology = methodologyRepository.findByNameIgnoreCase(methodologyName)
                    .map(m -> m.getIDMethodology())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la metodología: " + methodologyName));
                project.setIDMethodologyRef(idMethodology);
            } else {
                throw new IllegalArgumentException("Methodology name is required");
            }

            //  Buscar estado por nombre
            if (body.get("statusName") != null) {
                String statusName = (String) body.get("statusName");
                Integer idStatus = projectStatusRepository.findByNameIgnoreCase(statusName)
                    .map(s -> s.getIDProjectStatus())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el estado: " + statusName));
                project.setIDProjectStatusRef(idStatus);
            } else {
                throw new IllegalArgumentException("Status name is required");
            }

            // Fechas opcionales
            if (body.get("startDate") != null)
                project.setStartDate(LocalDate.parse((String) body.get("startDate")));
            if (body.get("endDate") != null)
                project.setEndDate(LocalDate.parse((String) body.get("endDate")));

            Project created = projectService.createProject(project);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Integer id, @RequestBody Project project) {
        try {
            Project updated = projectService.updateProject(id, project);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Integer id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project logically deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
