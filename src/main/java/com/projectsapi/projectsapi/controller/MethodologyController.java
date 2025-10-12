package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;

import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/methodologies")
public class MethodologyController {

    @Autowired
    private MethodologyRepository methodologyRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public List<Methodology> getAll() {
        return methodologyRepository.findAll();
    }

    // ✅ Nuevo endpoint: roles por metodología
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<Role>> getRolesByMethodology(@PathVariable Integer id) {
        if (!methodologyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roleRepository.findByMethodology_IDMethodology(id));
    }
}
