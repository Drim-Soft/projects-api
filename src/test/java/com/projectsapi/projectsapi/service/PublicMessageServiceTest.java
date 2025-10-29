package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.PublicMessage;
import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.repository.PublicMessageRepository;
import com.projectsapi.projectsapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicMessageServiceTest {

    @Mock
    private PublicMessageRepository publicMessageRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private PublicMessageService publicMessageService;

    private PublicMessage testMessage;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setIDTask(1);
        testTask.setName("Tarea de Prueba");

        testMessage = new PublicMessage();
        testMessage.setIDPublicMessage(1);
        testMessage.setContent("Mensaje de prueba");
        testMessage.setIDUser(1);
        testMessage.setIDTaskRef(1);
        testMessage.setDate(LocalDateTime.now());
    }

    @Test
    void testGetAllPublicMessages() {
        // Given
        List<PublicMessage> expectedMessages = Arrays.asList(testMessage);
        when(publicMessageRepository.findAll()).thenReturn(expectedMessages);

        // When
        List<PublicMessage> result = publicMessageService.getAllPublicMessages();

        // Then
        assertEquals(1, result.size());
        assertEquals(testMessage.getContent(), result.get(0).getContent());
        verify(publicMessageRepository).findAll();
    }

    @Test
    void testGetPublicMessageById() {
        // Given
        when(publicMessageRepository.findById(1)).thenReturn(Optional.of(testMessage));

        // When
        Optional<PublicMessage> result = publicMessageService.getPublicMessageById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMessage.getContent(), result.get().getContent());
        verify(publicMessageRepository).findById(1);
    }

    @Test
    void testGetPublicMessagesByTaskId() {
        // Given
        List<PublicMessage> expectedMessages = Arrays.asList(testMessage);
        when(publicMessageRepository.findByTaskIDTaskOrderByDateDesc(1)).thenReturn(expectedMessages);

        // When
        List<PublicMessage> result = publicMessageService.getPublicMessagesByTaskId(1);

        // Then
        assertEquals(1, result.size());
        assertEquals(testMessage.getContent(), result.get(0).getContent());
        verify(publicMessageRepository).findByTaskIDTaskOrderByDateDesc(1);
    }

    @Test
    void testCreatePublicMessage_Success() {
        // Given
        PublicMessage newMessage = new PublicMessage();
        newMessage.setContent("Nuevo mensaje");
        newMessage.setIDUser(1);
        newMessage.setIDTaskRef(1);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(publicMessageRepository.save(any(PublicMessage.class))).thenReturn(newMessage);

        // When
        PublicMessage result = publicMessageService.createPublicMessage(newMessage);

        // Then
        assertNotNull(result);
        assertEquals("Nuevo mensaje", result.getContent());
        assertEquals(testTask, result.getTask());
        verify(publicMessageRepository).save(newMessage);
    }

    @Test
    void testCreatePublicMessage_TaskNotFound() {
        // Given
        PublicMessage newMessage = new PublicMessage();
        newMessage.setContent("Nuevo mensaje");
        newMessage.setIDUser(1);
        newMessage.setIDTaskRef(999);
        
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> publicMessageService.createPublicMessage(newMessage));
        assertEquals("No se encontró la tarea con ID: 999", exception.getMessage());
    }

    @Test
    void testCreatePublicMessage_EmptyContent() {
        // Given
        PublicMessage newMessage = new PublicMessage();
        newMessage.setContent("");
        newMessage.setIDUser(1);
        newMessage.setIDTaskRef(1);
        
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> publicMessageService.createPublicMessage(newMessage));
        assertEquals("El contenido del mensaje es requerido", exception.getMessage());
    }

    @Test
    void testUpdatePublicMessage_Success() {
        // Given
        PublicMessage updatedMessage = new PublicMessage();
        updatedMessage.setContent("Mensaje actualizado");
        
        when(publicMessageRepository.findById(1)).thenReturn(Optional.of(testMessage));
        when(publicMessageRepository.save(any(PublicMessage.class))).thenReturn(testMessage);

        // When
        PublicMessage result = publicMessageService.updatePublicMessage(1, updatedMessage);

        // Then
        assertNotNull(result);
        assertEquals("Mensaje actualizado", result.getContent());
        verify(publicMessageRepository).save(testMessage);
    }

    @Test
    void testDeletePublicMessage_Success() {
        // Given
        when(publicMessageRepository.existsById(1)).thenReturn(true);

        // When
        publicMessageService.deletePublicMessage(1);

        // Then
        verify(publicMessageRepository).deleteById(1);
    }

    @Test
    void testCountMessagesByTaskId() {
        // Given
        when(publicMessageRepository.countByTaskIDTask(1)).thenReturn(5L);

        // When
        long result = publicMessageService.countMessagesByTaskId(1);

        // Then
        assertEquals(5L, result);
        verify(publicMessageRepository).countByTaskIDTask(1);
    }
}
