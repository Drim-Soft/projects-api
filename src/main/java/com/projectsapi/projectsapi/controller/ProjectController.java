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
import java.util.Collections;

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

            // Metodología
            if (body.get("methodologyName") != null) {
                String methodologyName = (String) body.get("methodologyName");
                Integer idMethodology = methodologyRepository.findByNameIgnoreCase(methodologyName)
                    .map(m -> m.getIDMethodology())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la metodología: " + methodologyName));
                project.setIDMethodologyRef(idMethodology);
            }

            // Estado
            if (body.get("statusName") != null) {
                String statusName = (String) body.get("statusName");
                Integer idStatus = projectStatusRepository.findByNameIgnoreCase(statusName)
                    .map(s -> s.getIDProjectStatus())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el estado: " + statusName));
                project.setIDProjectStatusRef(idStatus);
            }

            // Fechas opcionales
            if (body.get("startDate") != null)
                project.setStartDate(LocalDate.parse((String) body.get("startDate")));
            if (body.get("endDate") != null)
                project.setEndDate(LocalDate.parse((String) body.get("endDate")));

            // Guardar proyecto
            Project created = projectService.createProject(project);

            // Asociar usuario creador en UserRoleProject
            if (body.get("userId") != null) {
                Integer userId = (Integer) body.get("userId");
                projectService.assignUserToProject(userId, created.getIDProject(), "Administrador Proyecto");
            }

            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Project project = new Project();

            System.out.println("=== 🔧 INICIANDO UPDATE PROJECT ===");
            System.out.println("Body recibido: " + body);

            // Campos básicos
            if (body.get("name") != null)
                project.setName((String) body.get("name"));

            if (body.get("description") != null)
                project.setDescription((String) body.get("description"));

            // Fecha fin (puede venir en formato string)
            if (body.get("endDate") != null) {
                String endDateStr = body.get("endDate").toString();
                System.out.println("Fecha fin recibida: " + endDateStr);
                project.setEndDate(LocalDate.parse(endDateStr));
            }

            // Campos numéricos (Integer)
            if (body.get("budget") != null)
                project.setBudget(Integer.parseInt(body.get("budget").toString()));

            if (body.get("cost") != null)
                project.setCost(Integer.parseInt(body.get("cost").toString()));

            if (body.get("percentageProgress") != null)
                project.setPercentageProgress(Integer.parseInt(body.get("percentageProgress").toString()));

            if (body.get("percentageBudgetExecution") != null)
                project.setPercentageBudgetExecution(Integer.parseInt(body.get("percentageBudgetExecution").toString()));

            // 🔹 Estado (statusName → IDProjectStatusRef)
            if (body.get("statusName") != null) {
                String statusName = (String) body.get("statusName");
                System.out.println("Buscando estado por nombre: " + statusName);

                Integer idStatus = projectStatusRepository.findByNameIgnoreCase(statusName)
                    .map(s -> s.getIDProjectStatus())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el estado: " + statusName));

                project.setIDProjectStatusRef(idStatus);
                System.out.println("ID de estado encontrado: " + idStatus);
            }

            // 🔹 Metodología (opcional, por si también se manda)
            if (body.get("methodologyName") != null) {
                String methodologyName = (String) body.get("methodologyName");
                System.out.println("Buscando metodología: " + methodologyName);

                Integer idMethodology = methodologyRepository.findByNameIgnoreCase(methodologyName)
                    .map(m -> m.getIDMethodology())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la metodología: " + methodologyName));

                project.setIDMethodologyRef(idMethodology);
                System.out.println("ID de metodología encontrado: " + idMethodology);
            }

            // 🔹 Llamar servicio
            Project updated = projectService.updateProject(id, project);
            System.out.println("✅ Proyecto actualizado correctamente: " + updated.getName());
            System.out.println("=== ✅ FIN UPDATE PROJECT ===");

            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Error de argumento: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("💥 Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
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
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getProjectsByUserId(@PathVariable Integer userId) {
        try {
            List<Project> projects = projectService.getProjectsByUserId(userId);
            if (projects.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(projects);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

}
