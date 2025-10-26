package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.service.TaskService;
import com.projectsapi.projectsapi.service.StorageService;
import com.projectsapi.projectsapi.repository.TaskStatusRepository;
import com.projectsapi.projectsapi.repository.TaskPriorityRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/tasks")
public class TaskController {


    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Integer id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Integer userId) {
        return taskService.getTasksByUser(userId);
    }

    @GetMapping("/phase/{phaseId}")
    public List<Task> getTasksByPhase(@PathVariable Integer phaseId) {
        return taskService.getTasksByPhase(phaseId);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Map<String, Object> body) {
        try {

            Task task = new Task();
            task.setName((String) body.get("name"));
            task.setDescription((String) body.get("description"));

            // Buscar fase por ID
            if (body.get("phaseId") != null) {
                Integer phaseId = (Integer) body.get("phaseId");
                task.setIDPhaseRef(phaseId);
            } else {
                throw new IllegalArgumentException("Phase ID is required");
            }

            // Buscar estado por nombre
            if (body.get("statusName") != null) {
                String statusName = (String) body.get("statusName");
                Integer idStatus = taskStatusRepository.findByNameIgnoreCase(statusName)
                    .map(s -> s.getIDTaskStatus())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el estado: " + statusName));
                task.setIDTaskStatusRef(idStatus);
            } else {
                throw new IllegalArgumentException("Status name is required");
            }

            // Buscar prioridad por nombre
            if (body.get("priorityName") != null) {
                String priorityName = (String) body.get("priorityName");
                Integer idPriority = taskPriorityRepository.findByNameIgnoreCase(priorityName)
                    .map(p -> p.getIDTaskPriority())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró la prioridad: " + priorityName));
                task.setIDTaskPriorityRef(idPriority);
            } else {
                throw new IllegalArgumentException("Priority name is required");
            }

            // Campos opcionales
            if (body.get("userId") != null)
                task.setIDUser((Integer) body.get("userId"));
            
            if (body.get("startDate") != null)
                task.setStartDate(LocalDate.parse((String) body.get("startDate")));
            
            if (body.get("endDate") != null)
                task.setEndDate(LocalDate.parse((String) body.get("endDate")));
            
            if (body.get("timeInvested") != null)
                task.setTimeInvested((Integer) body.get("timeInvested"));
            
            if (body.get("percentageProgress") != null)
                task.setPercentageProgress((Integer) body.get("percentageProgress"));
            
            if (body.get("budget") != null)
                task.setBudget((Integer) body.get("budget"));
            
            if (body.get("cost") != null)
                task.setCost((Integer) body.get("cost"));
            
            if (body.get("fileURL") != null)
                task.setFileURL((String) body.get("fileURL"));
            
            if (body.get("score") != null)
                task.setScore((Integer) body.get("score"));
            
            if (body.get("feedback") != null)
                task.setFeedback((String) body.get("feedback"));

            Task created = taskService.createTask(task);
            return ResponseEntity.ok(created);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Integer id, @RequestBody Task task) {
        try {
            Task updated = taskService.updateTask(id, task);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task logically deleted");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{taskId}/upload-file")
    public ResponseEntity<?> uploadTaskFile(
            @PathVariable Integer taskId,
            @RequestParam("file") MultipartFile file) {

        return storageService.uploadAndLinkFileToTask(taskId, file);
    }

    @PostMapping(value = "/create-with-file", consumes = "multipart/form-data")
    public ResponseEntity<?> createTaskWithFile(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("phaseId") Integer phaseId,
            @RequestParam("statusName") String statusName,
            @RequestParam("priorityName") String priorityName,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "timeInvested", required = false) Integer timeInvested,
            @RequestParam(value = "percentageProgress", required = false) Integer percentageProgress,
            @RequestParam(value = "budget", required = false) Integer budget,
            @RequestParam(value = "cost", required = false) Integer cost,
            @RequestParam(value = "score", required = false) Integer score,
            @RequestParam(value = "feedback", required = false) String feedback) {

        try {
            // Crear la tarea primero
            Task task = new Task();
            task.setName(name);
            task.setDescription(description);
            task.setIDPhaseRef(phaseId);

            // Buscar estado por nombre
            Integer idStatus = taskStatusRepository.findByNameIgnoreCase(statusName)
                .map(s -> s.getIDTaskStatus())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estado: " + statusName));
            task.setIDTaskStatusRef(idStatus);

            // Buscar prioridad por nombre
            Integer idPriority = taskPriorityRepository.findByNameIgnoreCase(priorityName)
                .map(p -> p.getIDTaskPriority())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la prioridad: " + priorityName));
            task.setIDTaskPriorityRef(idPriority);

            // Campos opcionales
            if (userId != null) task.setIDUser(userId);
            if (startDate != null) task.setStartDate(LocalDate.parse(startDate));
            if (endDate != null) task.setEndDate(LocalDate.parse(endDate));
            if (timeInvested != null) task.setTimeInvested(timeInvested);
            if (percentageProgress != null) task.setPercentageProgress(percentageProgress);
            if (budget != null) task.setBudget(budget);
            if (cost != null) task.setCost(cost);
            if (score != null) task.setScore(score);
            if (feedback != null) task.setFeedback(feedback);

            // Crear la tarea
            Task createdTask = taskService.createTask(task);

            // Si hay archivo, subirlo y actualizar la tarea
            if (file != null && !file.isEmpty()) {
                ResponseEntity<?> uploadResponse = storageService.uploadAndLinkFileToTask(createdTask.getIDTask(), file);
                if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
                    return uploadResponse; // Retornar error de subida
                }
            }

            return ResponseEntity.ok(createdTask);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
}
