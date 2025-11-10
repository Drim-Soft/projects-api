package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.model.UserRoleProject;
import com.projectsapi.projectsapi.repository.ProjectRepository;
import com.projectsapi.projectsapi.repository.RoleRepository;
import com.projectsapi.projectsapi.repository.UserRoleProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleProjectServiceTest {

    @Mock
    private UserRoleProjectRepository userRoleProjectRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private UserRoleProjectService userRoleProjectService;

    private UserRoleProject testUserRoleProject;
    private Project testProject;
    private Role testRole;
    private Methodology testMethodology;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Metodología Ágil");

        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setName("Proyecto de Prueba");
        testProject.setMethodology(testMethodology);

        testRole = new Role();
        testRole.setIDRole(1);
        testRole.setName("Administrador Proyecto");
        testRole.setMethodology(testMethodology);

        testUserRoleProject = new UserRoleProject();
        testUserRoleProject.setIDUser(1);
        testUserRoleProject.setIDRole(1);
        testUserRoleProject.setIDProject(1);
    }

    @Test
    void testGetUsersByProject() {
        // Given
        List<UserRoleProject> expectedRelations = Arrays.asList(testUserRoleProject);
        when(userRoleProjectRepository.findByIDProject(1)).thenReturn(expectedRelations);

        // When
        List<UserRoleProject> result = userRoleProjectService.getUsersByProject(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testUserRoleProject.getIDUser(), result.get(0).getIDUser());
        verify(userRoleProjectRepository).findByIDProject(1);
    }

    @Test
    void testGetUserIdsByProject() {
        // Given
        List<UserRoleProject> expectedRelations = Arrays.asList(testUserRoleProject);
        when(userRoleProjectRepository.findByIDProject(1)).thenReturn(expectedRelations);

        // When
        List<Integer> result = userRoleProjectService.getUserIdsByProject(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testUserRoleProject.getIDUser(), result.get(0));
        verify(userRoleProjectRepository).findByIDProject(1);
    }

    @Test
    void testAssignUserToProject_Success() {
        // Given
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(userRoleProjectRepository.save(any(UserRoleProject.class))).thenReturn(testUserRoleProject);

        // When
        UserRoleProject result = userRoleProjectService.assignUserToProject(1, 1, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getIDUser());
        assertEquals(1, result.getIDRole());
        assertEquals(1, result.getIDProject());
        verify(userRoleProjectRepository).save(any(UserRoleProject.class));
    }

    @Test
    void testAssignUserToProject_ProjectNotFound() {
        // Given
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userRoleProjectService.assignUserToProject(999, 1, 1));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_RoleNotFound() {
        // Given
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userRoleProjectService.assignUserToProject(1, 1, 999));
        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_ProjectHasNoMethodology() {
        // Given
        Project projectWithoutMethodology = new Project();
        projectWithoutMethodology.setIDProject(1);
        projectWithoutMethodology.setMethodology(null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(projectWithoutMethodology));
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userRoleProjectService.assignUserToProject(1, 1, 1));
        assertEquals("Project has no methodology assigned", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_RoleNotValidForMethodology() {
        // Given
        Methodology differentMethodology = new Methodology();
        differentMethodology.setIDMethodology(2);
        differentMethodology.setName("Metodología Diferente");

        Role roleFromDifferentMethodology = new Role();
        roleFromDifferentMethodology.setIDRole(1);
        roleFromDifferentMethodology.setName("Administrador Proyecto");
        roleFromDifferentMethodology.setMethodology(differentMethodology);

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findById(1)).thenReturn(Optional.of(roleFromDifferentMethodology));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> userRoleProjectService.assignUserToProject(1, 1, 1));
        assertEquals("This role is not valid for the project's methodology", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_ValidAssignment() {
        // Given
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(userRoleProjectRepository.save(any(UserRoleProject.class))).thenReturn(testUserRoleProject);

        // When
        UserRoleProject result = userRoleProjectService.assignUserToProject(1, 1, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getIDUser());
        assertEquals(1, result.getIDRole());
        assertEquals(1, result.getIDProject());
        verify(userRoleProjectRepository).save(any(UserRoleProject.class));
    }
}
