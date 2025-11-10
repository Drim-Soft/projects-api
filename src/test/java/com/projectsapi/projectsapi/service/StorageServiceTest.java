package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private StorageService storageService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setIDTask(1);
        testTask.setName("Tarea de Prueba");
    }

    @Test
    void testUploadAndLinkFileToTask_TaskNotFound() {
        // When
        ResponseEntity<?> response = storageService.uploadAndLinkFileToTask(1, multipartFile);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void testUploadAndLinkFileToTask_NullFile() {
        // When
        ResponseEntity<?> response = storageService.uploadAndLinkFileToTask(1, null);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void testUploadAndLinkFileToTask_InvalidTaskId() {
        // When
        ResponseEntity<?> response = storageService.uploadAndLinkFileToTask(null, multipartFile);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Error"));
    }
}
