package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.*;
import com.projectsapi.projectsapi.repository.TaskStatusRepository;
import com.projectsapi.projectsapi.repository.TaskPriorityRepository;
import com.projectsapi.projectsapi.service.TaskService;
import com.projectsapi.projectsapi.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private StorageService storageService;

    @MockBean
    private TaskStatusRepository taskStatusRepository;

    @MockBean
    private TaskPriorityRepository taskPriorityRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;
    private TaskStatus testStatus;
    private TaskPriority testPriority;

    @BeforeEach
    void setUp() {
        testStatus = new TaskStatus();
        testStatus.setIDTaskStatus(1);
        testStatus.setName("En Progreso");

        testPriority = new TaskPriority();
        testPriority.setIDTaskPriority(1);
        testPriority.setName("Alta");

        testTask = new Task();
        testTask.setIDTask(1);
        testTask.setName("Tarea Test");
        testTask.setDescription("Descripción");
        testTask.setIDPhaseRef(1);
        testTask.setIDTaskStatusRef(1);
        testTask.setIDTaskPriorityRef(1);
    }

    @Test
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tarea Test"));

        verify(taskService).getAllTasks();
    }

    @Test
    void testGetTaskById_Success() throws Exception {
        when(taskService.getTaskById(1)).thenReturn(Optional.of(testTask));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tarea Test"));

        verify(taskService).getTaskById(1);
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService).getTaskById(999);
    }

    @Test
    void testGetTasksByUser() throws Exception {
        when(taskService.getTasksByUser(1)).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tarea Test"));

        verify(taskService).getTasksByUser(1);
    }

    @Test
    void testGetTasksByPhase() throws Exception {
        when(taskService.getTasksByPhase(1)).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/tasks/phase/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Tarea Test"));

        verify(taskService).getTasksByPhase(1);
    }

    @Test
    void testCreateTask_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Nueva Tarea");
        body.put("description", "Descripción");
        body.put("phaseId", 1);
        body.put("statusName", "En Progreso");
        body.put("priorityName", "Alta");
        body.put("userId", 1);
        body.put("startDate", "2025-01-01");
        body.put("endDate", "2025-01-31");

        when(taskStatusRepository.findByNameIgnoreCase("En Progreso")).thenReturn(Optional.of(testStatus));
        when(taskPriorityRepository.findByNameIgnoreCase("Alta")).thenReturn(Optional.of(testPriority));
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void testCreateTask_MissingPhaseId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Nueva Tarea");
        body.put("statusName", "En Progreso");
        body.put("priorityName", "Alta");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        when(taskService.updateTask(eq(1), any(Task.class))).thenReturn(testTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isOk());

        verify(taskService).updateTask(eq(1), any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() throws Exception {
        when(taskService.updateTask(eq(999), any(Task.class)))
                .thenThrow(new IllegalArgumentException("Task not found"));

        mockMvc.perform(put("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task logically deleted"));

        verify(taskService).deleteTask(1);
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Task not found")).when(taskService).deleteTask(999);

        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadTaskFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
        ResponseEntity<?> response = ResponseEntity.ok(Map.of("url", "http://test.com/file.txt"));
        doReturn(response).when(storageService).uploadAndLinkFileToTask(eq(1), any());

        mockMvc.perform(multipart("/tasks/1/upload-file")
                        .file(file))
                .andExpect(status().isOk());

        verify(storageService).uploadAndLinkFileToTask(eq(1), any());
    }

    @Test
    void testCreateTaskWithFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        when(taskStatusRepository.findByNameIgnoreCase("En Progreso")).thenReturn(Optional.of(testStatus));
        when(taskPriorityRepository.findByNameIgnoreCase("Alta")).thenReturn(Optional.of(testPriority));
        when(taskService.createTask(any(Task.class))).thenReturn(testTask);
        ResponseEntity<?> response = ResponseEntity.ok(Map.of("url", "http://test.com/file.txt"));
        doReturn(response).when(storageService).uploadAndLinkFileToTask(eq(1), any());

        mockMvc.perform(multipart("/tasks/create-with-file")
                        .file(file)
                        .param("name", "Tarea con Archivo")
                        .param("description", "Descripción")
                        .param("phaseId", "1")
                        .param("statusName", "En Progreso")
                        .param("priorityName", "Alta"))
                .andExpect(status().isOk());

        verify(taskService).createTask(any(Task.class));
    }
}

