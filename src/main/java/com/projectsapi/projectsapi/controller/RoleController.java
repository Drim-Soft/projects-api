package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;

import java.util.*;

@RestController
@RequestMapping("/projects/{projectId}/roles")
public class RoleController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public ResponseEntity<?> getRolesByProject(@PathVariable Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (project.getMethodology() == null) {
            return ResponseEntity.status(404).body("Project has no methodology assigned");
        }

        List<Role> roles = roleRepository.findByMethodology_IDMethodology(
                project.getMethodology().getIDMethodology()
        );

        return ResponseEntity.ok(roles);
    }
}
