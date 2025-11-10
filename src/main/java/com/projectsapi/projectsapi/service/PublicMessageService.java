package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projectsapi.projectsapi.model.PublicMessage;
import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.repository.PublicMessageRepository;
import com.projectsapi.projectsapi.repository.TaskRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class PublicMessageService {

    @Autowired
    private PublicMessageRepository publicMessageRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<PublicMessage> getAllPublicMessages() {
        return publicMessageRepository.findAll();
    }

    public Optional<PublicMessage> getPublicMessageById(Integer id) {
        return publicMessageRepository.findById(id);
    }

    public List<PublicMessage> getPublicMessagesByTaskId(Integer taskId) {
        return publicMessageRepository.findByTaskIDTaskOrderByDateDesc(taskId);
    }

    public List<PublicMessage> getPublicMessagesByUserId(Integer userId) {
        return publicMessageRepository.findByIDUserOrderByDateDesc(userId);
    }

    public List<PublicMessage> getPublicMessagesByTaskAndUser(Integer taskId, Integer userId) {
        return publicMessageRepository.findByTaskIDTaskAndIDUserOrderByDateDesc(taskId, userId);
    }

    public long countMessagesByTaskId(Integer taskId) {
        return publicMessageRepository.countByTaskIDTask(taskId);
    }

    public PublicMessage createPublicMessage(PublicMessage publicMessage) {
        // Validar que la tarea existe
        if (publicMessage.getIDTaskRef() != null) {
            Optional<Task> task = taskRepository.findById(publicMessage.getIDTaskRef());
            if (task.isPresent()) {
                publicMessage.setTask(task.get());
            } else {
                throw new IllegalArgumentException("No se encontró la tarea con ID: " + publicMessage.getIDTaskRef());
            }
        } else {
            throw new IllegalArgumentException("ID de tarea es requerido");
        }

        // Validar que el contenido no esté vacío
        if (publicMessage.getContent() == null || publicMessage.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje es requerido");
        }

        // Validar que el usuario esté especificado
        if (publicMessage.getIDUser() == null) {
            throw new IllegalArgumentException("ID de usuario es requerido");
        }

        // Establecer fecha actual si no está establecida
        if (publicMessage.getDate() == null) {
            publicMessage.setDate(LocalDateTime.now());
        }

        return publicMessageRepository.save(publicMessage);
    }

    public PublicMessage updatePublicMessage(Integer id, PublicMessage publicMessage) {
        Optional<PublicMessage> existingMessage = publicMessageRepository.findById(id);
        
        if (existingMessage.isPresent()) {
            PublicMessage messageToUpdate = existingMessage.get();
            
            // Actualizar solo los campos permitidos
            if (publicMessage.getContent() != null && !publicMessage.getContent().trim().isEmpty()) {
                messageToUpdate.setContent(publicMessage.getContent());
            }
            
            return publicMessageRepository.save(messageToUpdate);
        } else {
            throw new IllegalArgumentException("No se encontró el mensaje con ID: " + id);
        }
    }

    public void deletePublicMessage(Integer id) {
        if (publicMessageRepository.existsById(id)) {
            publicMessageRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("No se encontró el mensaje con ID: " + id);
        }
    }
}
