package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.*;
import com.projectsapi.projectsapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MethodologyRepository methodologyRepository;

    @Mock
    private ProjectStatusRepository projectStatusRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleProjectRepository userRoleProjectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private Methodology testMethodology;
    private ProjectStatus testStatus;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Metodología Ágil");

        testStatus = new ProjectStatus();
        testStatus.setIDProjectStatus(1);
        testStatus.setName("En Progreso");

        testRole = new Role();
        testRole.setIDRole(1);
        testRole.setName("Administrador Proyecto");
        testRole.setMethodology(testMethodology);

        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setName("Proyecto de Prueba");
        testProject.setDescription("Descripción del proyecto");
        testProject.setStartDate(LocalDate.now());
        testProject.setEndDate(LocalDate.now().plusMonths(6));
        testProject.setBudget(100000);
        testProject.setCost(50000);
        testProject.setPercentageProgress(50);
        testProject.setPercentageBudgetExecution(50);
        testProject.setIDMethodologyRef(1);
        testProject.setIDProjectStatusRef(1);
    }

    @Test
    void testGetAllProjects() {
        // Given
        List<Project> expectedProjects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(expectedProjects);

        // When
        List<Project> result = projectService.getAllProjects();

        // Then
        assertEquals(1, result.size());
        assertEquals(testProject.getName(), result.get(0).getName());
        verify(projectRepository).findAll();
    }

    @Test
    void testGetProjectById() {
        // Given
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));

        // When
        Optional<Project> result = projectService.getProjectById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProject.getName(), result.get().getName());
        verify(projectRepository).findById(1);
    }

    @Test
    void testGetProjectById_NotFound() {
        // Given
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Project> result = projectService.getProjectById(999);

        // Then
        assertFalse(result.isPresent());
        verify(projectRepository).findById(999);
    }

    @Test
    void testCreateProject_Success() {
        // Given
        when(methodologyRepository.findById(1)).thenReturn(Optional.of(testMethodology));
        when(projectStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(Arrays.asList(testRole));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        Project result = projectService.createProject(testProject);

        // Then
        assertNotNull(result);
        assertEquals(testMethodology, result.getMethodology());
        assertEquals(testStatus, result.getProjectStatus());
        verify(projectRepository).save(testProject);
        verify(userRoleProjectRepository).assignUserToProject(any(), any(), any());
    }

    @Test
    void testCreateProject_MissingMethodologyId() {
        // Given
        testProject.setIDMethodologyRef(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.createProject(testProject));
        assertEquals("Methodology ID is required", exception.getMessage());
    }

    @Test
    void testCreateProject_MissingProjectStatusId() {
        // Given
        testProject.setIDProjectStatusRef(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.createProject(testProject));
        assertEquals("ProjectStatus ID is required", exception.getMessage());
    }

    @Test
    void testCreateProject_InvalidMethodologyId() {
        // Given
        testProject.setIDMethodologyRef(999); // Cambiar el ID para que coincida con el mock
        when(methodologyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.createProject(testProject));
        assertEquals("Invalid Methodology ID", exception.getMessage());
    }

    @Test
    void testUpdateProject_Success() {
        // Given
        Project updatedProject = new Project();
        updatedProject.setName("Proyecto Actualizado");
        updatedProject.setDescription("Nueva descripción");
        updatedProject.setBudget(150000);

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        Project result = projectService.updateProject(1, updatedProject);

        // Then
        assertNotNull(result);
        assertEquals("Proyecto Actualizado", result.getName());
        assertEquals("Nueva descripción", result.getDescription());
        assertEquals(150000, result.getBudget());
        verify(projectRepository).save(testProject);
    }

    @Test
    void testUpdateProject_NotFound() {
        // Given
        Project updatedProject = new Project();
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.updateProject(999, updatedProject));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testDeleteProject_Success() {
        // Given
        ProjectStatus deletedStatus = new ProjectStatus();
        deletedStatus.setIDProjectStatus(2);
        deletedStatus.setName("Eliminado");

        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(projectStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus, deletedStatus));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        projectService.deleteProject(1);

        // Then
        verify(projectRepository).save(testProject);
        assertEquals(deletedStatus, testProject.getProjectStatus());
    }

    @Test
    void testDeleteProject_NotFound() {
        // Given
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.deleteProject(999));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testGetProjectsByUserId() {
        // Given
        List<Project> expectedProjects = Arrays.asList(testProject);
        when(projectRepository.findProjectsByUserId(1)).thenReturn(expectedProjects);

        // When
        List<Project> result = projectService.getProjectsByUserId(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProject.getName(), result.get(0).getName());
        verify(projectRepository).findProjectsByUserId(1);
    }

    @Test
    void testGetProjectsByUserId_NullUserId() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.getProjectsByUserId(null));
        assertEquals("User ID cannot be null", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_Success() {
        // Given
        testProject.setMethodology(testMethodology); // Asegurar que el proyecto tenga metodología
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(Arrays.asList(testRole));
        when(userRoleProjectRepository.findByIDProject(1)).thenReturn(Arrays.asList());

        // When
        projectService.assignUserToProject(1, 1, "Administrador Proyecto");

        // Then
        verify(userRoleProjectRepository).assignUserToProject(1, 1, 1);
    }

    @Test
    void testAssignUserToProject_ProjectNotFound() {
        // Given
        when(projectRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.assignUserToProject(1, 999, "Administrador Proyecto"));
        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testAssignUserToProject_RoleNotFound() {
        // Given
        testProject.setMethodology(testMethodology); // Asegurar que el proyecto tenga metodología
        when(projectRepository.findById(1)).thenReturn(Optional.of(testProject));
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(Arrays.asList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectService.assignUserToProject(1, 1, "Rol Inexistente"));
        assertTrue(exception.getMessage().contains("Role not found"));
    }
}
