package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.model.Project;
import com.projectsapi.projectsapi.service.ProjectService;
import com.projectsapi.projectsapi.service.ProjectUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectUserController.class)
class ProjectUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectUserService projectUserService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private Project testProject;
    private Methodology testMethodology;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Ágil");

        testProject = new Project();
        testProject.setIDProject(1);
        testProject.setName("Proyecto Test");
        testProject.setMethodology(testMethodology);
    }

    @Test
    void testAssignUserToProject() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);
        body.put("IDRole", 2);

        doNothing().when(projectUserService).insertRelation(1, 2, 1);

        mockMvc.perform(patch("/projects/1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectUserService).insertRelation(1, 2, 1);
    }

    @Test
    void testGetUsersByProject() throws Exception {
        Map<String, Object> user1 = new HashMap<>();
        user1.put("IDUser", 1);
        user1.put("IDProject", 1);

        when(jdbcTemplate.queryForList(anyString(), anyInt())).thenReturn(Arrays.asList(user1));

        mockMvc.perform(get("/projects/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].IDUser").value(1));

        verify(jdbcTemplate).queryForList(anyString(), eq(1));
    }

    @Test
    void testJoinProject_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);

        when(projectService.getProjectById(1)).thenReturn(Optional.of(testProject));
        doNothing().when(projectUserService).insertRelation(anyInt(), anyInt(), anyInt());

        mockMvc.perform(post("/projects/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectService).getProjectById(1);
        verify(projectUserService).insertRelation(eq(1), anyInt(), eq(1));
    }

    @Test
    void testJoinProject_MissingUserId() throws Exception {
        Map<String, Object> body = new HashMap<>();

        mockMvc.perform(post("/projects/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testJoinProject_ProjectNotFound() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);

        when(projectService.getProjectById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/projects/999/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testJoinProject_WithMethodologyId1() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);

        testProject.setIDMethodologyRef(1);
        when(projectService.getProjectById(1)).thenReturn(Optional.of(testProject));
        doNothing().when(projectUserService).insertRelation(anyInt(), anyInt(), anyInt());

        mockMvc.perform(post("/projects/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectUserService).insertRelation(eq(1), eq(25), eq(1));
    }

    @Test
    void testJoinProject_WithMethodologyId2() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);

        testProject.setMethodology(null);
        testProject.setIDMethodologyRef(2);
        when(projectService.getProjectById(1)).thenReturn(Optional.of(testProject));
        doNothing().when(projectUserService).insertRelation(anyInt(), anyInt(), anyInt());

        mockMvc.perform(post("/projects/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(projectUserService).insertRelation(eq(1), eq(26), eq(1));
    }
}

