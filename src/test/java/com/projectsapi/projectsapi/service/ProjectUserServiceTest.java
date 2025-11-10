package com.projectsapi.projectsapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectUserServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProjectUserService projectUserService;

    @Test
    void testInsertRelation_Success() {
        // Given
        Integer userId = 1;
        Integer roleId = 2;
        Integer projectId = 3;

        when(jdbcTemplate.update(anyString(), any(), any(), any())).thenReturn(1);

        // When
        projectUserService.insertRelation(userId, roleId, projectId);

        // Then
        verify(jdbcTemplate).update(
            "INSERT INTO UserRoleProject (IDUser, IDRole, IDProject) VALUES (?, ?, ?)",
            userId, roleId, projectId
        );
    }

    @Test
    void testInsertRelation_NullUserId() {
        // Given
        Integer userId = null;
        Integer roleId = 2;
        Integer projectId = 3;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectUserService.insertRelation(userId, roleId, projectId));
        assertEquals(" Los campos IDUser, IDRole e IDProject son obligatorios.", exception.getMessage());
    }

    @Test
    void testInsertRelation_NullRoleId() {
        // Given
        Integer userId = 1;
        Integer roleId = null;
        Integer projectId = 3;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectUserService.insertRelation(userId, roleId, projectId));
        assertEquals(" Los campos IDUser, IDRole e IDProject son obligatorios.", exception.getMessage());
    }

    @Test
    void testInsertRelation_NullProjectId() {
        // Given
        Integer userId = 1;
        Integer roleId = 2;
        Integer projectId = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectUserService.insertRelation(userId, roleId, projectId));
        assertEquals(" Los campos IDUser, IDRole e IDProject son obligatorios.", exception.getMessage());
    }

    @Test
    void testInsertRelation_AllNull() {
        // Given
        Integer userId = null;
        Integer roleId = null;
        Integer projectId = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> projectUserService.insertRelation(userId, roleId, projectId));
        assertEquals(" Los campos IDUser, IDRole e IDProject son obligatorios.", exception.getMessage());
    }

    @Test
    void testInsertRelation_ValidValues() {
        // Given
        Integer userId = 10;
        Integer roleId = 20;
        Integer projectId = 30;

        when(jdbcTemplate.update(anyString(), any(), any(), any())).thenReturn(1);

        // When
        projectUserService.insertRelation(userId, roleId, projectId);

        // Then
        verify(jdbcTemplate).update(
            "INSERT INTO UserRoleProject (IDUser, IDRole, IDProject) VALUES (?, ?, ?)",
            userId, roleId, projectId
        );
    }
}
