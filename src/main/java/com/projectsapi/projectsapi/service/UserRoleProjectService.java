package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.model.UserRoleProject;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;
import com.projectsapi.projectsapi.repository.UserRoleProjectRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserRoleProjectService {

    @Autowired
    private UserRoleProjectRepository userRoleProjectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Listar relaciones completas por ID de proyecto
    public List<UserRoleProject> getUsersByProject(Integer projectId) {
        return userRoleProjectRepository.findByIDProject(projectId);
    }

    // Solo IDs de usuarios (si lo necesitas)
    public List<Integer> getUserIdsByProject(Integer projectId) {
        return userRoleProjectRepository.findByIDProject(projectId)
                .stream()
                .map(UserRoleProject::getIDUser)
                .collect(Collectors.toList());
    }

    // Asignar usuario a proyecto con rol
    public UserRoleProject assignUserToProject(Integer projectId, Integer userId, Integer roleId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        // Verificar que el proyecto tenga metodología
        if (project.getMethodology() == null) {
            throw new IllegalArgumentException("Project has no methodology assigned");
        }

        // Verificar que el rol pertenezca a esa metodología
        if (!Objects.equals(
                role.getMethodology().getIDMethodology(),
                project.getMethodology().getIDMethodology())) {
            throw new IllegalArgumentException("This role is not valid for the project's methodology");
        }

        // Crear la relación directa
        UserRoleProject relation = new UserRoleProject();
        relation.setIDUser(userId);
        relation.setIDRole(roleId);
        relation.setIDProject(projectId);

        return userRoleProjectRepository.save(relation);
    }
}
