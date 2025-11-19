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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PhaseRepository phaseRepository;

    @Mock
    private TaskStatusRepository taskStatusRepository;

    @Mock
    private TaskPriorityRepository taskPriorityRepository;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private Phase testPhase;
    private TaskStatus testStatus;
    private TaskPriority testPriority;

    @BeforeEach
    void setUp() {
        testPhase = new Phase();
        testPhase.setIDPhase(1);
        testPhase.setName("Fase de Desarrollo");

        testStatus = new TaskStatus();
        testStatus.setIDTaskStatus(1);
        testStatus.setName("En Progreso");

        testPriority = new TaskPriority();
        testPriority.setIDTaskPriority(1);
        testPriority.setName("Alta");

        testTask = new Task();
        testTask.setIDTask(1);
        testTask.setName("Tarea de Prueba");
        testTask.setDescription("Descripción de la tarea");
        testTask.setStartDate(LocalDate.now());
        testTask.setEndDate(LocalDate.now().plusDays(7));
        testTask.setIDPhaseRef(1);
        testTask.setIDTaskStatusRef(1);
        testTask.setIDTaskPriorityRef(1);
        testTask.setIDUser(1);
    }

    @Test
    void testGetAllTasks() {
        // Given
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getAllTasks();

        // Then
        assertEquals(1, result.size());
        assertEquals(testTask.getName(), result.get(0).getName());
        verify(taskRepository).findAll();
    }

    @Test
    void testGetTaskById() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));

        // When
        Optional<Task> result = taskService.getTaskById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTask.getName(), result.get().getName());
        verify(taskRepository).findById(1);
    }

    @Test
    void testCreateTask_Success() {
        // Given
        when(phaseRepository.findById(1)).thenReturn(Optional.of(testPhase));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.of(testPriority));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.createTask(testTask);

        // Then
        assertNotNull(result);
        assertEquals(testPhase, result.getPhase());
        assertEquals(testStatus, result.getTaskStatus());
        assertEquals(testPriority, result.getTaskPriority());
        verify(taskRepository).save(testTask);
    }

    @Test
    void testCreateTask_InvalidPhaseId() {
        // Given
        when(phaseRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.createTask(testTask));
        assertEquals("Invalid Phase ID", exception.getMessage());
    }

    @Test
    void testUpdateTask_Success() {
        // Given
        Task updatedTask = new Task();
        updatedTask.setName("Tarea Actualizada");
        updatedTask.setDescription("Nueva descripción");
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(1, updatedTask);

        // Then
        assertNotNull(result);
        assertEquals("Tarea Actualizada", result.getName());
        assertEquals("Nueva descripción", result.getDescription());
        verify(taskRepository).save(testTask);
    }

    @Test
    void testDeleteTask_Success() {
        // Given
        TaskStatus deletedStatus = new TaskStatus();
        deletedStatus.setIDTaskStatus(2);
        deletedStatus.setName("Eliminado");
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus, deletedStatus));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        taskService.deleteTask(1);

        // Then
        verify(taskRepository).save(testTask);
        assertEquals(deletedStatus, testTask.getTaskStatus());
    }

    @Test
    void testGetTasksByUser() {
        // Given
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(taskRepository.findByIDUser(1)).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getTasksByUser(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTask.getName(), result.get(0).getName());
        verify(taskRepository).findByIDUser(1);
    }

    @Test
    void testGetTasksByPhase() {
        // Given
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(taskRepository.findByPhase_IDPhase(1)).thenReturn(expectedTasks);

        // When
        List<Task> result = taskService.getTasksByPhase(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTask.getName(), result.get(0).getName());
        verify(taskRepository).findByPhase_IDPhase(1);
    }

    @Test
    void testCreateTask_InvalidTaskStatusId() {
        // Given
        when(phaseRepository.findById(1)).thenReturn(Optional.of(testPhase));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.createTask(testTask));
        assertEquals("Invalid TaskStatus ID", exception.getMessage());
    }

    @Test
    void testCreateTask_InvalidTaskPriorityId() {
        // Given
        when(phaseRepository.findById(1)).thenReturn(Optional.of(testPhase));
        when(taskStatusRepository.findById(1)).thenReturn(Optional.of(testStatus));
        when(taskPriorityRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.createTask(testTask));
        assertEquals("Invalid TaskPriority ID", exception.getMessage());
    }

    @Test
    void testCreateTask_MissingTaskStatusId() {
        // Given
        testTask.setIDTaskStatusRef(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.createTask(testTask));
        assertEquals("TaskStatus ID is required", exception.getMessage());
    }

    @Test
    void testCreateTask_MissingTaskPriorityId() {
        // Given
        testTask.setIDTaskPriorityRef(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.createTask(testTask));
        assertEquals("TaskPriority ID is required", exception.getMessage());
    }

    @Test
    void testUpdateTask_WithPhaseRelation() {
        // Given
        Phase newPhase = new Phase();
        newPhase.setIDPhase(2);
        
        Task updatedTask = new Task();
        updatedTask.setName("Tarea Actualizada");
        updatedTask.setIDPhaseRef(2);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(phaseRepository.findById(2)).thenReturn(Optional.of(newPhase));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(1, updatedTask);

        // Then
        assertNotNull(result);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testUpdateTask_WithTaskStatusRelation() {
        // Given
        TaskStatus newStatus = new TaskStatus();
        newStatus.setIDTaskStatus(2);
        
        Task updatedTask = new Task();
        updatedTask.setName("Tarea Actualizada");
        updatedTask.setIDTaskStatusRef(2);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskStatusRepository.findById(2)).thenReturn(Optional.of(newStatus));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(1, updatedTask);

        // Then
        assertNotNull(result);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testUpdateTask_WithTaskPriorityRelation() {
        // Given
        TaskPriority newPriority = new TaskPriority();
        newPriority.setIDTaskPriority(2);
        
        Task updatedTask = new Task();
        updatedTask.setName("Tarea Actualizada");
        updatedTask.setIDTaskPriorityRef(2);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskPriorityRepository.findById(2)).thenReturn(Optional.of(newPriority));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // When
        Task result = taskService.updateTask(1, updatedTask);

        // Then
        assertNotNull(result);
        verify(taskRepository).save(testTask);
    }

    @Test
    void testUpdateTask_InvalidPhaseId() {
        // Given
        Task updatedTask = new Task();
        updatedTask.setIDPhaseRef(999);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(phaseRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.updateTask(1, updatedTask));
        assertEquals("Invalid Phase ID", exception.getMessage());
    }

    @Test
    void testUpdateTask_NotFound() {
        // Given
        Task updatedTask = new Task();
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.updateTask(999, updatedTask));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void testDeleteTask_NotFound() {
        // Given
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.deleteTask(999));
        assertEquals("Task not found", exception.getMessage());
    }

    @Test
    void testDeleteTask_DeletedStatusNotFound() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(taskStatusRepository.findAll()).thenReturn(Arrays.asList(testStatus));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> taskService.deleteTask(1));
        assertEquals("Deleted status not found", exception.getMessage());
    }
}
