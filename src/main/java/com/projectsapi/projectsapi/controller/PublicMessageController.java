package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.PublicMessage;
import com.projectsapi.projectsapi.service.PublicMessageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/public-messages")
public class PublicMessageController {

    @Autowired
    private PublicMessageService publicMessageService;

    @GetMapping
    public List<PublicMessage> getAllPublicMessages() {
        return publicMessageService.getAllPublicMessages();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicMessage> getPublicMessageById(@PathVariable Integer id) {
        Optional<PublicMessage> publicMessage = publicMessageService.getPublicMessageById(id);
        return publicMessage.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/task/{taskId}")
    public List<PublicMessage> getPublicMessagesByTaskId(@PathVariable Integer taskId) {
        return publicMessageService.getPublicMessagesByTaskId(taskId);
    }

    @GetMapping("/user/{userId}")
    public List<PublicMessage> getPublicMessagesByUserId(@PathVariable Integer userId) {
        return publicMessageService.getPublicMessagesByUserId(userId);
    }

    @GetMapping("/task/{taskId}/user/{userId}")
    public List<PublicMessage> getPublicMessagesByTaskAndUser(@PathVariable Integer taskId, @PathVariable Integer userId) {
        return publicMessageService.getPublicMessagesByTaskAndUser(taskId, userId);
    }

    @GetMapping("/task/{taskId}/count")
    public ResponseEntity<Long> countMessagesByTaskId(@PathVariable Integer taskId) {
        long count = publicMessageService.countMessagesByTaskId(taskId);
        return ResponseEntity.ok(count);
    }

    @PostMapping
    public ResponseEntity<?> createPublicMessage(@RequestBody Map<String, Object> body) {
        try {
            PublicMessage publicMessage = new PublicMessage();
            
            // Validar y establecer ID de usuario
            if (body.get("IDUser") != null) {
                publicMessage.setIDUser((Integer) body.get("IDUser"));
            } else {
                throw new IllegalArgumentException("ID de usuario es requerido");
            }

            // Validar y establecer ID de tarea
            if (body.get("IDTaskRef") != null) {
                publicMessage.setIDTaskRef((Integer) body.get("IDTaskRef"));
            } else {
                throw new IllegalArgumentException("ID de tarea es requerido");
            }

            // Validar y establecer contenido
            if (body.get("content") != null) {
                publicMessage.setContent((String) body.get("content"));
            } else {
                throw new IllegalArgumentException("Contenido del mensaje es requerido");
            }

            PublicMessage created = publicMessageService.createPublicMessage(publicMessage);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePublicMessage(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            PublicMessage publicMessage = new PublicMessage();
            
            // Solo permitir actualizar el contenido
            if (body.get("content") != null) {
                publicMessage.setContent((String) body.get("content"));
            } else {
                throw new IllegalArgumentException("Contenido del mensaje es requerido para actualizar");
            }

            PublicMessage updated = publicMessageService.updatePublicMessage(id, publicMessage);
            return ResponseEntity.ok(updated);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublicMessage(@PathVariable Integer id) {
        try {
            publicMessageService.deletePublicMessage(id);
            return ResponseEntity.ok("Mensaje público eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
}
