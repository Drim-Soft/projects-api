package com.projectsapi.projectsapi.controller;

import com.projectsapi.projectsapi.model.TaskStatus;
import com.projectsapi.projectsapi.service.TaskStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskStatusController.class)
class TaskStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskStatusService taskStatusService;

    private TaskStatus testStatus;

    @BeforeEach
    void setUp() {
        testStatus = new TaskStatus();
        testStatus.setIDTaskStatus(1);
        testStatus.setName("En Progreso");
    }

    @Test
    void testTestEndpoint() throws Exception {
        mockMvc.perform(get("/task-status/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("TaskStatus controller is working!"));
    }

    @Test
    void testGetAllTaskStatuses() throws Exception {
        when(taskStatusService.getAllTaskStatuses()).thenReturn(Arrays.asList(testStatus));

        mockMvc.perform(get("/task-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("En Progreso"));

        verify(taskStatusService).getAllTaskStatuses();
    }

    @Test
    void testGetTaskStatusById_Success() throws Exception {
        when(taskStatusService.getTaskStatusById(1)).thenReturn(Optional.of(testStatus));

        mockMvc.perform(get("/task-status/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("En Progreso"));

        verify(taskStatusService).getTaskStatusById(1);
    }

    @Test
    void testGetTaskStatusById_NotFound() throws Exception {
        when(taskStatusService.getTaskStatusById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/task-status/999"))
                .andExpect(status().isNotFound());

        verify(taskStatusService).getTaskStatusById(999);
    }

    @Test
    void testGetTaskStatusByName_Success() throws Exception {
        when(taskStatusService.getTaskStatusByName("En Progreso")).thenReturn(Optional.of(testStatus));

        mockMvc.perform(get("/task-status/name/En Progreso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("En Progreso"));

        verify(taskStatusService).getTaskStatusByName("En Progreso");
    }

    @Test
    void testCreateTaskStatus() throws Exception {
        when(taskStatusService.createTaskStatus(any(TaskStatus.class))).thenReturn(testStatus);

        mockMvc.perform(post("/task-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"En Progreso\"}"))
                .andExpect(status().isCreated());

        verify(taskStatusService).createTaskStatus(any(TaskStatus.class));
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        when(taskStatusService.updateTaskStatus(eq(1), any(TaskStatus.class))).thenReturn(testStatus);

        mockMvc.perform(put("/task-status/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Actualizado\"}"))
                .andExpect(status().isOk());

        verify(taskStatusService).updateTaskStatus(eq(1), any(TaskStatus.class));
    }

    @Test
    void testDeleteTaskStatus() throws Exception {
        doNothing().when(taskStatusService).deleteTaskStatus(1);

        mockMvc.perform(delete("/task-status/1"))
                .andExpect(status().isNoContent());

        verify(taskStatusService).deleteTaskStatus(1);
    }

    @Test
    void testExistsById() throws Exception {
        when(taskStatusService.existsById(1)).thenReturn(true);

        mockMvc.perform(get("/task-status/exists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(taskStatusService).existsById(1);
    }

    @Test
    void testExistsByName() throws Exception {
        when(taskStatusService.existsByName("En Progreso")).thenReturn(true);

        mockMvc.perform(get("/task-status/exists/name/En Progreso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(taskStatusService).existsByName("En Progreso");
    }
}

