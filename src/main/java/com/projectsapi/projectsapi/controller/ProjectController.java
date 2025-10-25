package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.service.ProjectService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

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
            // ✅ Se separa el usuario creador del resto del cuerpo del proyecto
            Integer idUser = (Integer) body.get("IDUser");

            // Crear el objeto Project con el resto de los campos
            Project project = new Project();
            project.setName((String) body.get("name"));
            project.setDescription((String) body.get("description"));
            project.setIDMethodologyRef((Integer) body.get("IDMethodologyRef"));
            project.setIDProjectStatusRef((Integer) body.get("IDProjectStatusRef"));

            // Fechas opcionales
            if (body.get("startDate") != null)
                project.setStartDate(java.sql.Timestamp.valueOf((String) body.get("startDate")));
            if (body.get("endDate") != null)
                project.setEndDate(java.sql.Timestamp.valueOf((String) body.get("endDate")));

            // Agregamos temporalmente el ID del creador en ProjectStatusRef (para usarlo dentro del servicio)
            project.setIDProjectStatusRef((Integer) body.get("IDProjectStatusRef"));
            // ⚠️ truco temporal: ProjectService usará ese valor para capturar el creador.
            // Luego se reemplazará cuando integremos el microservicio de usuarios.

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
