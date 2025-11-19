package com.projectsapi.projectsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectsapi.projectsapi.model.PublicMessage;
import com.projectsapi.projectsapi.service.PublicMessageService;
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

@WebMvcTest(PublicMessageController.class)
class PublicMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicMessageService publicMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    private PublicMessage testMessage;

    @BeforeEach
    void setUp() {
        testMessage = new PublicMessage();
        testMessage.setIDPublicMessage(1);
        testMessage.setIDUser(1);
        testMessage.setIDTaskRef(1);
        testMessage.setContent("Mensaje de prueba");
    }

    @Test
    void testGetAllPublicMessages() throws Exception {
        when(publicMessageService.getAllPublicMessages()).thenReturn(Arrays.asList(testMessage));

        mockMvc.perform(get("/public-messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Mensaje de prueba"));

        verify(publicMessageService).getAllPublicMessages();
    }

    @Test
    void testGetPublicMessageById_Success() throws Exception {
        when(publicMessageService.getPublicMessageById(1)).thenReturn(Optional.of(testMessage));

        mockMvc.perform(get("/public-messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Mensaje de prueba"));

        verify(publicMessageService).getPublicMessageById(1);
    }

    @Test
    void testGetPublicMessageById_NotFound() throws Exception {
        when(publicMessageService.getPublicMessageById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/public-messages/999"))
                .andExpect(status().isNotFound());

        verify(publicMessageService).getPublicMessageById(999);
    }

    @Test
    void testGetPublicMessagesByTaskId() throws Exception {
        when(publicMessageService.getPublicMessagesByTaskId(1)).thenReturn(Arrays.asList(testMessage));

        mockMvc.perform(get("/public-messages/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Mensaje de prueba"));

        verify(publicMessageService).getPublicMessagesByTaskId(1);
    }

    @Test
    void testGetPublicMessagesByUserId() throws Exception {
        when(publicMessageService.getPublicMessagesByUserId(1)).thenReturn(Arrays.asList(testMessage));

        mockMvc.perform(get("/public-messages/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Mensaje de prueba"));

        verify(publicMessageService).getPublicMessagesByUserId(1);
    }

    @Test
    void testGetPublicMessagesByTaskAndUser() throws Exception {
        when(publicMessageService.getPublicMessagesByTaskAndUser(1, 1)).thenReturn(Arrays.asList(testMessage));

        mockMvc.perform(get("/public-messages/task/1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Mensaje de prueba"));

        verify(publicMessageService).getPublicMessagesByTaskAndUser(1, 1);
    }

    @Test
    void testCountMessagesByTaskId() throws Exception {
        when(publicMessageService.countMessagesByTaskId(1)).thenReturn(5L);

        mockMvc.perform(get("/public-messages/task/1/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(publicMessageService).countMessagesByTaskId(1);
    }

    @Test
    void testCreatePublicMessage_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDUser", 1);
        body.put("IDTaskRef", 1);
        body.put("content", "Nuevo mensaje");

        when(publicMessageService.createPublicMessage(any(PublicMessage.class))).thenReturn(testMessage);

        mockMvc.perform(post("/public-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(publicMessageService).createPublicMessage(any(PublicMessage.class));
    }

    @Test
    void testCreatePublicMessage_MissingUserId() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("IDTaskRef", 1);
        body.put("content", "Nuevo mensaje");

        mockMvc.perform(post("/public-messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdatePublicMessage_Success() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("content", "Mensaje actualizado");

        when(publicMessageService.updatePublicMessage(eq(1), any(PublicMessage.class))).thenReturn(testMessage);

        mockMvc.perform(put("/public-messages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        verify(publicMessageService).updatePublicMessage(eq(1), any(PublicMessage.class));
    }

    @Test
    void testUpdatePublicMessage_MissingContent() throws Exception {
        Map<String, Object> body = new HashMap<>();

        mockMvc.perform(put("/public-messages/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeletePublicMessage_Success() throws Exception {
        doNothing().when(publicMessageService).deletePublicMessage(1);

        mockMvc.perform(delete("/public-messages/1"))
                .andExpect(status().isOk());

        verify(publicMessageService).deletePublicMessage(1);
    }

    @Test
    void testDeletePublicMessage_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Message not found")).when(publicMessageService).deletePublicMessage(999);

        mockMvc.perform(delete("/public-messages/999"))
                .andExpect(status().isBadRequest());
    }
}

