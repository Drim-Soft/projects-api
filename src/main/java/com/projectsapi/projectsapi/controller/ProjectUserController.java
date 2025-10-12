package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.projectsapi.projectsapi.service.ProjectUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectUserController {

    private final ProjectUserService projectUserService;
      @Autowired
    private JdbcTemplate jdbcTemplate;

    public ProjectUserController(ProjectUserService projectUserService) {
        this.projectUserService = projectUserService;
    }

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
}
