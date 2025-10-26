package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.UserTask;
import com.projectsapi.projectsapi.model.UserTaskId;
import com.projectsapi.projectsapi.repository.UserTaskRepository;
import com.projectsapi.projectsapi.repository.TaskRepository;
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
class UserTaskServiceTest {

    @Mock
    private UserTaskRepository userTaskRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserTaskService userTaskService;

    private UserTask testUserTask;
    private UserTaskId testUserTaskId;

    @BeforeEach
    void setUp() {
        testUserTaskId = new UserTaskId(1, 1);
        testUserTask = new UserTask(1, 1);
    }

    @Test
    void testGetAllUserTasks() {
        // Given
        List<UserTask> expectedUserTasks = Arrays.asList(testUserTask);
        when(userTaskRepository.findAll()).thenReturn(expectedUserTasks);

        // When
        List<UserTask> result = userTaskService.getAllUserTasks();

        // Then
        assertEquals(1, result.size());
        assertEquals(testUserTask.getIDUser(), result.get(0).getIDUser());
        assertEquals(testUserTask.getIDTask(), result.get(0).getIDTask());
        verify(userTaskRepository).findAll();
    }

    @Test
    void testGetUserTaskById() {
        // Given
        when(userTaskRepository.findById(testUserTaskId)).thenReturn(Optional.of(testUserTask));

        // When
        Optional<UserTask> result = userTaskService.getUserTaskById(1, 1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUserTask.getIDUser(), result.get().getIDUser());
        assertEquals(testUserTask.getIDTask(), result.get().getIDTask());
        verify(userTaskRepository).findById(testUserTaskId);
    }

    @Test
    void testGetUserTasksByUserId() {
        // Given
        List<UserTask> expectedUserTasks = Arrays.asList(testUserTask);
        when(userTaskRepository.findByIDUser(1)).thenReturn(expectedUserTasks);

        // When
        List<UserTask> result = userTaskService.getUserTasksByUserId(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testUserTask.getIDUser(), result.get(0).getIDUser());
        verify(userTaskRepository).findByIDUser(1);
    }

    @Test
    void testGetUserTasksByTaskId() {
        // Given
        List<UserTask> expectedUserTasks = Arrays.asList(testUserTask);
        when(userTaskRepository.findByIDTask(1)).thenReturn(expectedUserTasks);

        // When
        List<UserTask> result = userTaskService.getUserTasksByTaskId(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testUserTask.getIDTask(), result.get(0).getIDTask());
        verify(userTaskRepository).findByIDTask(1);
    }

    @Test
    void testAssignUserToTask_Success() {
        // Given
        when(taskRepository.existsById(1)).thenReturn(true);
        when(userTaskRepository.existsByIDUserAndIDTask(1, 1)).thenReturn(false);
        when(userTaskRepository.save(any(UserTask.class))).thenReturn(testUserTask);

        // When
        UserTask result = userTaskService.assignUserToTask(1, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getIDUser());
        assertEquals(1, result.getIDTask());
        verify(userTaskRepository).save(any(UserTask.class));
    }

    @Test
    void testAssignUserToTask_TaskNotFound() {
        // Given
        when(taskRepository.existsById(999)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userTaskService.assignUserToTask(1, 999));
        assertEquals("No se encontró la tarea con ID: 999", exception.getMessage());
    }

    @Test
    void testAssignUserToTask_AlreadyAssigned() {
        // Given
        when(taskRepository.existsById(1)).thenReturn(true);
        when(userTaskRepository.existsByIDUserAndIDTask(1, 1)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userTaskService.assignUserToTask(1, 1));
        assertEquals("El usuario ya está asignado a esta tarea", exception.getMessage());
    }

    @Test
    void testUnassignUserFromTask_Success() {
        // Given
        when(userTaskRepository.existsByIDUserAndIDTask(1, 1)).thenReturn(true);

        // When
        userTaskService.unassignUserFromTask(1, 1);

        // Then
        verify(userTaskRepository).deleteByIDUserAndIDTask(1, 1);
    }

    @Test
    void testUnassignUserFromTask_NotAssigned() {
        // Given
        when(userTaskRepository.existsByIDUserAndIDTask(1, 1)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> userTaskService.unassignUserFromTask(1, 1));
        assertEquals("No se encontró la asignación del usuario a esta tarea", exception.getMessage());
    }

    @Test
    void testExistsUserTaskRelation() {
        // Given
        when(userTaskRepository.existsByIDUserAndIDTask(1, 1)).thenReturn(true);

        // When
        boolean result = userTaskService.existsUserTaskRelation(1, 1);

        // Then
        assertTrue(result);
        verify(userTaskRepository).existsByIDUserAndIDTask(1, 1);
    }

    @Test
    void testCountUsersByTaskId() {
        // Given
        when(userTaskRepository.countByIDTask(1)).thenReturn(3L);

        // When
        long result = userTaskService.countUsersByTaskId(1);

        // Then
        assertEquals(3L, result);
        verify(userTaskRepository).countByIDTask(1);
    }

    @Test
    void testCountTasksByUserId() {
        // Given
        when(userTaskRepository.countByIDUser(1)).thenReturn(5L);

        // When
        long result = userTaskService.countTasksByUserId(1);

        // Then
        assertEquals(5L, result);
        verify(userTaskRepository).countByIDUser(1);
    }
}
