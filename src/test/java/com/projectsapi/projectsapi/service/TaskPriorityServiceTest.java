package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.TaskPriority;
import com.projectsapi.projectsapi.repository.TaskPriorityRepository;
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
class TaskPriorityServiceTest {

    @Mock
    private TaskPriorityRepository taskPriorityRepository;

    @InjectMocks
    private TaskPriorityService taskPriorityService;

    private TaskPriority testTaskPriority;

    @BeforeEach
    void setUp() {
        testTaskPriority = new TaskPriority();
        testTaskPriority.setIDTaskPriority(1);
        testTaskPriority.setName("Alta");
    }

    @Test
    void testGetAllTaskPriorities() {
        // Given
        List<TaskPriority> expectedPriorities = Arrays.asList(testTaskPriority);
        when(taskPriorityRepository.findAll()).thenReturn(expectedPriorities);

        // When
        List<TaskPriority> result = taskPriorityService.getAllTaskPriorities();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTaskPriority.getName(), result.get(0).getName());
        verify(taskPriorityRepository).findAll();
    }

    @Test
    void testGetTaskPriorityById() {
        // Given
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testTaskPriority));

        // When
        Optional<TaskPriority> result = taskPriorityService.getTaskPriorityById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTaskPriority.getName(), result.get().getName());
        verify(taskPriorityRepository).findById(1);
    }

    @Test
    void testGetTaskPriorityByName() {
        // Given
        when(taskPriorityRepository.findByNameIgnoreCase("Alta")).thenReturn(Optional.of(testTaskPriority));

        // When
        Optional<TaskPriority> result = taskPriorityService.getTaskPriorityByName("Alta");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTaskPriority.getName(), result.get().getName());
        verify(taskPriorityRepository).findByNameIgnoreCase("Alta");
    }

    @Test
    void testCreateTaskPriority_Success() {
        // Given
        TaskPriority newPriority = new TaskPriority();
        newPriority.setName("Crítica");
        
        when(taskPriorityRepository.findByNameIgnoreCase("Crítica")).thenReturn(Optional.empty());
        when(taskPriorityRepository.save(any(TaskPriority.class))).thenReturn(newPriority);

        // When
        TaskPriority result = taskPriorityService.createTaskPriority(newPriority);

        // Then
        assertNotNull(result);
        assertEquals("Crítica", result.getName());
        verify(taskPriorityRepository).save(newPriority);
    }

    @Test
    void testCreateTaskPriority_DuplicateName() {
        // Given
        TaskPriority newPriority = new TaskPriority();
        newPriority.setName("Alta");
        
        when(taskPriorityRepository.findByNameIgnoreCase("Alta")).thenReturn(Optional.of(testTaskPriority));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskPriorityService.createTaskPriority(newPriority));
        assertEquals("Task priority with name 'Alta' already exists", exception.getMessage());
    }

    @Test
    void testCreateTaskPriority_EmptyName() {
        // Given
        TaskPriority newPriority = new TaskPriority();
        newPriority.setName("");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskPriorityService.createTaskPriority(newPriority));
        assertEquals("Task priority name is required", exception.getMessage());
    }

    @Test
    void testUpdateTaskPriority_Success() {
        // Given
        TaskPriority updatedPriority = new TaskPriority();
        updatedPriority.setName("Muy Alta");
        
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testTaskPriority));
        when(taskPriorityRepository.findByNameIgnoreCase("Muy Alta")).thenReturn(Optional.empty());
        when(taskPriorityRepository.save(any(TaskPriority.class))).thenReturn(testTaskPriority);

        // When
        TaskPriority result = taskPriorityService.updateTaskPriority(1, updatedPriority);

        // Then
        assertNotNull(result);
        assertEquals("Muy Alta", result.getName());
        verify(taskPriorityRepository).save(testTaskPriority);
    }

    @Test
    void testDeleteTaskPriority_Success() {
        // Given
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testTaskPriority));

        // When
        taskPriorityService.deleteTaskPriority(1);

        // Then
        verify(taskPriorityRepository).delete(testTaskPriority);
    }

    @Test
    void testExistsByName() {
        // Given
        when(taskPriorityRepository.findByNameIgnoreCase("Alta")).thenReturn(Optional.of(testTaskPriority));

        // When
        boolean result = taskPriorityService.existsByName("Alta");

        // Then
        assertTrue(result);
        verify(taskPriorityRepository).findByNameIgnoreCase("Alta");
    }
}
