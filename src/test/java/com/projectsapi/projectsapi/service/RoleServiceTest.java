package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Role;
import com.projectsapi.projectsapi.repository.RoleRepository;
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
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private Methodology testMethodology;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Metodología Ágil");

        testRole = new Role();
        testRole.setIDRole(1);
        testRole.setName("Administrador Proyecto");
        testRole.setMethodology(testMethodology);
    }

    @Test
    void testGetAllRoles() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole);
        when(roleRepository.findAll()).thenReturn(expectedRoles);

        // When
        List<Role> result = roleService.getAllRoles();

        // Then
        assertEquals(1, result.size());
        assertEquals(testRole.getName(), result.get(0).getName());
        verify(roleRepository).findAll();
    }

    @Test
    void testGetRoleById() {
        // Given
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));

        // When
        Optional<Role> result = roleService.getRoleById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testRole.getName(), result.get().getName());
        verify(roleRepository).findById(1);
    }

    @Test
    void testGetRoleById_NotFound() {
        // Given
        when(roleRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Role> result = roleService.getRoleById(999);

        // Then
        assertFalse(result.isPresent());
        verify(roleRepository).findById(999);
    }

    @Test
    void testGetRolesByMethodology() {
        // Given
        List<Role> expectedRoles = Arrays.asList(testRole);
        when(roleRepository.findByMethodology_IDMethodology(1)).thenReturn(expectedRoles);

        // When
        List<Role> result = roleService.getRolesByMethodology(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testRole.getName(), result.get(0).getName());
        verify(roleRepository).findByMethodology_IDMethodology(1);
    }

    @Test
    void testCreateRole() {
        // Given
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // When
        Role result = roleService.createRole(testRole);

        // Then
        assertNotNull(result);
        assertEquals(testRole.getName(), result.getName());
        verify(roleRepository).save(testRole);
    }

    @Test
    void testUpdateRole_Success() {
        // Given
        Role updatedRole = new Role();
        updatedRole.setName("Rol Actualizado");

        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));
        when(roleRepository.save(any(Role.class))).thenReturn(testRole);

        // When
        Role result = roleService.updateRole(1, updatedRole);

        // Then
        assertNotNull(result);
        assertEquals("Rol Actualizado", result.getName());
        verify(roleRepository).save(testRole);
    }

    @Test
    void testUpdateRole_NotFound() {
        // Given
        Role updatedRole = new Role();
        when(roleRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> roleService.updateRole(999, updatedRole));
        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void testDeleteRole_Success() {
        // Given
        when(roleRepository.findById(1)).thenReturn(Optional.of(testRole));

        // When
        roleService.deleteRole(1);

        // Then
        verify(roleRepository).deleteById(1);
    }

    @Test
    void testDeleteRole_NotFound() {
        // Given
        when(roleRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> roleService.deleteRole(999));
        assertEquals("Role not found", exception.getMessage());
    }
}
