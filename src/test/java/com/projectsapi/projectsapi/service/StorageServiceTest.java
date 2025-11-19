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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private StorageService storageService;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setIDTask(1);
        testTask.setName("Tarea de Prueba");

        // Configurar propiedades usando ReflectionTestUtils
        ReflectionTestUtils.setField(storageService, "storageEndpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(storageService, "accessKey", "test-access-key");
        ReflectionTestUtils.setField(storageService, "secretKey", "test-secret-key");
        ReflectionTestUtils.setField(storageService, "region", "us-east-1");
    }

    @Test
    void testUploadAndLinkFileToTask_TaskNotFound() {
        // Given
        when(taskRepository.findById(1)).thenReturn(Optional.empty());

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

    @Test
    void testUploadAndLinkFileToTask_ExceptionHandling() throws Exception {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getSize()).thenReturn(100L);
        when(multipartFile.getInputStream()).thenThrow(new RuntimeException("IO Error"));

        // When
        ResponseEntity<?> response = storageService.uploadAndLinkFileToTask(1, multipartFile);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Error"));
    }

    @Test
    void testUploadAndLinkFileToTask_FileWithEmptyName() throws Exception {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));

        // When
        ResponseEntity<?> response = storageService.uploadAndLinkFileToTask(1, multipartFile);

        // Then
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
    }
}
