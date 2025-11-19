package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.UserTask;
import com.projectsapi.projectsapi.service.UserTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTaskController.class)
class UserTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserTaskService userTaskService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserTask testUserTask;

    @BeforeEach
    void setUp() {
        testUserTask = new UserTask();
        testUserTask.setIDUser(1);
        testUserTask.setIDTask(1);
    }

    @Test
    void testGetAllUserTasks() throws Exception {
        when(userTaskService.getAllUserTasks()).thenReturn(Arrays.asList(testUserTask));

        mockMvc.perform(get("/user-tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].IDUser").value(1));

        verify(userTaskService).getAllUserTasks();
    }

    @Test
    void testGetUserTaskById_Success() throws Exception {
        when(userTaskService.getUserTaskById(1, 1)).thenReturn(Optional.of(testUserTask));

        mockMvc.perform(get("/user-tasks/user/1/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.IDUser").value(1));

        verify(userTaskService).getUserTaskById(1, 1);
    }

    @Test
    void testGetUserTaskById_NotFound() throws Exception {
        when(userTaskService.getUserTaskById(999, 999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/user-tasks/user/999/task/999"))
                .andExpect(status().isNotFound());

        verify(userTaskService).getUserTaskById(999, 999);
    }

    @Test
    void testGetUserTasksByUserId() throws Exception {
        when(userTaskService.getUserTasksByUserId(1)).thenReturn(Arrays.asList(testUserTask));

        mockMvc.perform(get("/user-tasks/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].IDUser").value(1));

        verify(userTaskService).getUserTasksByUserId(1);
    }

    @Test
    void testGetUserTasksByTaskId() throws Exception {
        when(userTaskService.getUserTasksByTaskId(1)).thenReturn(Arrays.asList(testUserTask));

        mockMvc.perform(get("/user-tasks/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].IDTask").value(1));

        verify(userTaskService).getUserTasksByTaskId(1);
    }

    @Test
    void testGetUserIdsByTaskId() throws Exception {
        when(userTaskService.getUserIdsByTaskId(1)).thenReturn(Arrays.asList(1, 2));

        mockMvc.perform(get("/user-tasks/task/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1));

        verify(userTaskService).getUserIdsByTaskId(1);
    }

    @Test
    void testGetTaskIdsByUserId() throws Exception {
        when(userTaskService.getTaskIdsByUserId(1)).thenReturn(Arrays.asList(1, 2));

        mockMvc.perform(get("/user-tasks/user/1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(1));

        verify(userTaskService).getTaskIdsByUserId(1);
    }

    @Test
    void testCountUsersByTaskId() throws Exception {
        when(userTaskService.countUsersByTaskId(1)).thenReturn(5L);

        mockMvc.perform(get("/user-tasks/task/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(userTaskService).countUsersByTaskId(1);
    }

    @Test
    void testCountTasksByUserId() throws Exception {
        when(userTaskService.countTasksByUserId(1)).thenReturn(3L);

        mockMvc.perform(get("/user-tasks/user/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(3));

        verify(userTaskService).countTasksByUserId(1);
    }

    @Test
    void testExistsUserTaskRelation() throws Exception {
        when(userTaskService.existsUserTaskRelation(1, 1)).thenReturn(true);

        mockMvc.perform(get("/user-tasks/exists/user/1/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(userTaskService).existsUserTaskRelation(1, 1);
    }

    @Test
    void testAssignUserToTask_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("userId", 1);
        body.put("taskId", 1);

        when(userTaskService.assignUserToTask(1, 1)).thenReturn(testUserTask);

        mockMvc.perform(post("/user-tasks/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(userTaskService).assignUserToTask(1, 1);
    }

    @Test
    void testAssignUserToTask_MissingUserId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("taskId", 1);

        mockMvc.perform(post("/user-tasks/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnassignUserFromTask() throws Exception {
        doNothing().when(userTaskService).unassignUserFromTask(1, 1);

        mockMvc.perform(delete("/user-tasks/unassign/user/1/task/1"))
                .andExpect(status().isOk());

        verify(userTaskService).unassignUserFromTask(1, 1);
    }

    @Test
    void testDeleteAllUserTasksByUserId() throws Exception {
        doNothing().when(userTaskService).deleteAllUserTasksByUserId(1);

        mockMvc.perform(delete("/user-tasks/user/1"))
                .andExpect(status().isOk());

        verify(userTaskService).deleteAllUserTasksByUserId(1);
    }

    @Test
    void testDeleteAllUserTasksByTaskId() throws Exception {
        doNothing().when(userTaskService).deleteAllUserTasksByTaskId(1);

        mockMvc.perform(delete("/user-tasks/task/1"))
                .andExpect(status().isOk());

        verify(userTaskService).deleteAllUserTasksByTaskId(1);
    }
}

