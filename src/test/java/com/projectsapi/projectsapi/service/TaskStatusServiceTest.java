package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.TaskStatus;
import com.projectsapi.projectsapi.repository.TaskStatusRepository;
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
class TaskStatusServiceTest {

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @InjectMocks
    private TaskStatusService taskStatusService;

    private TaskStatus testTaskStatus;

    @BeforeEach
    void setUp() {
        testTaskStatus = new TaskStatus();
        testTaskStatus.setIDTaskStatus(1);
        testTaskStatus.setName("En Progreso");
    }

    @Test
    void testGetAllTaskStatuses() {
        // Given
        List<TaskStatus> expectedStatuses = Arrays.asList(testTaskStatus);
        when(taskStatusRepository.findAll()).thenReturn(expectedStatuses);

        // When
        List<TaskStatus> result = taskStatusService.getAllTaskStatuses();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTaskStatus.getName(), result.get(0).getName());
        verify(taskStatusRepository).findAll();
    }

    @Test
    void testGetTaskStatusById() {
        // Given
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testTaskStatus));

        // When
        Optional<TaskStatus> result = taskStatusService.getTaskStatusById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTaskStatus.getName(), result.get().getName());
        verify(taskStatusRepository).findById(1);
    }

    @Test
    void testCreateTaskStatus_Success() {
        // Given
        TaskStatus newStatus = new TaskStatus();
        newStatus.setName("Nuevo Estado");
        
        when(taskStatusRepository.findByNameIgnoreCase("Nuevo Estado")).thenReturn(Optional.empty());
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(newStatus);

        // When
        TaskStatus result = taskStatusService.createTaskStatus(newStatus);

        // Then
        assertNotNull(result);
        assertEquals("Nuevo Estado", result.getName());
        verify(taskStatusRepository).save(newStatus);
    }

    @Test
    void testCreateTaskStatus_DuplicateName() {
        // Given
        TaskStatus newStatus = new TaskStatus();
        newStatus.setName("En Progreso");
        
        when(taskStatusRepository.findByNameIgnoreCase("En Progreso")).thenReturn(Optional.of(testTaskStatus));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskStatusService.createTaskStatus(newStatus));
        assertEquals("Task status with name 'En Progreso' already exists", exception.getMessage());
    }

    @Test
    void testUpdateTaskStatus_Success() {
        // Given
        TaskStatus updatedStatus = new TaskStatus();
        updatedStatus.setName("Estado Actualizado");
        
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testTaskStatus));
        when(taskStatusRepository.findByNameIgnoreCase("Estado Actualizado")).thenReturn(Optional.empty());
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(testTaskStatus);

        // When
        TaskStatus result = taskStatusService.updateTaskStatus(1, updatedStatus);

        // Then
        assertNotNull(result);
        assertEquals("Estado Actualizado", result.getName());
        verify(taskStatusRepository).save(testTaskStatus);
    }

    @Test
    void testDeleteTaskStatus_Success() {
        // Given
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testTaskStatus));

        // When
        taskStatusService.deleteTaskStatus(1);

        // Then
        verify(taskStatusRepository).delete(testTaskStatus);
    }

    @Test
    void testExistsById() {
        // Given
        when(taskStatusRepository.existsById(1)).thenReturn(true);

        // When
        boolean result = taskStatusService.existsById(1);

        // Then
        assertTrue(result);
        verify(taskStatusRepository).existsById(1);
    }
}
