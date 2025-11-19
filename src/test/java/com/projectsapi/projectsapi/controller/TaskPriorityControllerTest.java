package com.projectsapi.projectsapi.controller;

import com.projectsapi.projectsapi.model.TaskPriority;
import com.projectsapi.projectsapi.service.TaskPriorityService;
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

@WebMvcTest(TaskPriorityController.class)
class TaskPriorityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskPriorityService taskPriorityService;

    private TaskPriority testPriority;

    @BeforeEach
    void setUp() {
        testPriority = new TaskPriority();
        testPriority.setIDTaskPriority(1);
        testPriority.setName("Alta");
    }

    @Test
    void testGetAllTaskPriorities() throws Exception {
        when(taskPriorityService.getAllTaskPriorities()).thenReturn(Arrays.asList(testPriority));

        mockMvc.perform(get("/task-priority"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alta"));

        verify(taskPriorityService).getAllTaskPriorities();
    }

    @Test
    void testGetTaskPriorityById_Success() throws Exception {
        when(taskPriorityService.getTaskPriorityById(1)).thenReturn(Optional.of(testPriority));

        mockMvc.perform(get("/task-priority/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alta"));

        verify(taskPriorityService).getTaskPriorityById(1);
    }

    @Test
    void testGetTaskPriorityById_NotFound() throws Exception {
        when(taskPriorityService.getTaskPriorityById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/task-priority/999"))
                .andExpect(status().isNotFound());

        verify(taskPriorityService).getTaskPriorityById(999);
    }

    @Test
    void testGetTaskPriorityByName_Success() throws Exception {
        when(taskPriorityService.getTaskPriorityByName("Alta")).thenReturn(Optional.of(testPriority));

        mockMvc.perform(get("/task-priority/name/Alta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alta"));

        verify(taskPriorityService).getTaskPriorityByName("Alta");
    }

    @Test
    void testCreateTaskPriority() throws Exception {
        when(taskPriorityService.createTaskPriority(any(TaskPriority.class))).thenReturn(testPriority);

        mockMvc.perform(post("/task-priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alta\"}"))
                .andExpect(status().isOk());

        verify(taskPriorityService).createTaskPriority(any(TaskPriority.class));
    }

    @Test
    void testUpdateTaskPriority() throws Exception {
        when(taskPriorityService.updateTaskPriority(eq(1), any(TaskPriority.class))).thenReturn(testPriority);

        mockMvc.perform(put("/task-priority/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Actualizada\"}"))
                .andExpect(status().isOk());

        verify(taskPriorityService).updateTaskPriority(eq(1), any(TaskPriority.class));
    }

    @Test
    void testDeleteTaskPriority() throws Exception {
        doNothing().when(taskPriorityService).deleteTaskPriority(1);

        mockMvc.perform(delete("/task-priority/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task priority deleted successfully"));

        verify(taskPriorityService).deleteTaskPriority(1);
    }

    @Test
    void testExistsById() throws Exception {
        when(taskPriorityService.existsById(1)).thenReturn(true);

        mockMvc.perform(get("/task-priority/exists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(taskPriorityService).existsById(1);
    }

    @Test
    void testExistsByName() throws Exception {
        when(taskPriorityService.existsByName("Alta")).thenReturn(true);

        mockMvc.perform(get("/task-priority/exists/name/Alta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(taskPriorityService).existsByName("Alta");
    }
}

