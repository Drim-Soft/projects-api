package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.projectsapi.projectsapi.service.ProjectUserService;
import com.projectsapi.projectsapi.service.ProjectService;
import com.projectsapi.projectsapi.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectUserController {

    private final ProjectUserService projectUserService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectUserController.class);
      @Autowired
    private JdbcTemplate jdbcTemplate;

    public ProjectUserController(ProjectUserService projectUserService) {
        this.projectUserService = projectUserService;
    }

    @Autowired
    private ProjectService projectService;

    @PatchMapping("/{id}/users")
    public ResponseEntity<String> assignUserToProject(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {

        Integer idUser = (Integer) body.get("IDUser");
        Integer idRole = (Integer) body.get("IDRole");

        projectUserService.insertRelation(idUser, idRole, id);

        return ResponseEntity.ok(" Usuario " + idUser + " asignado al proyecto " + id + " con rol " + idRole);
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<Map<String, Object>>> getUsersByProject(@PathVariable Integer id) {
        String sql = "SELECT * FROM UserRoleProject WHERE IDProject = ?";
        List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, id);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinProject(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        Integer idUser = (Integer) body.get("IDUser");
        if (idUser == null) {
            // Log similar to: console.error('❌ Error: Usuario o ID de proyecto faltantes:', { user, projectId: project.IDProject });
            logger.error("❌ Error: Usuario o ID de proyecto faltantes: user={}, projectId={}", null, id);
            return ResponseEntity.badRequest().body("IDUser es requerido");
        }

        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Integer methodologyId = null;
        if (project.getMethodology() != null) {
            methodologyId = project.getMethodology().getIDMethodology();
        } else if (project.getIDMethodologyRef() != null) {
            methodologyId = project.getIDMethodologyRef();
        }

        // Map methodology -> guest role id (1->25, 2->26, 3->27, 4->28)
        Integer guestRoleId;
        if (methodologyId == null) {
            // Log missing methodology with project id
            logger.error("❌ Error: Usuario o ID de proyecto faltantes: user={}, projectId={}", idUser, project.getIDProject());
            return ResponseEntity.badRequest().body("No se pudo determinar la metodología del proyecto");
        }
        switch (methodologyId) {
            case 1: guestRoleId = 25; break;
            case 2: guestRoleId = 26; break;
            case 3: guestRoleId = 27; break;
            case 4: guestRoleId = 28; break;
            default: guestRoleId = 25; // fallback
        }

        projectUserService.insertRelation(idUser, guestRoleId, id);

        return ResponseEntity.ok("Usuario " + idUser + " se unió al proyecto " + id + " como invitado (rol " + guestRoleId + ")");
    }
}
