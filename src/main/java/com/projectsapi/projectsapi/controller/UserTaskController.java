package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.UserTask;
import com.projectsapi.projectsapi.service.UserTaskService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user-tasks")
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;

    @GetMapping
    public List<UserTask> getAllUserTasks() {
        return userTaskService.getAllUserTasks();
    }

    @GetMapping("/user/{userId}/task/{taskId}")
    public ResponseEntity<UserTask> getUserTaskById(@PathVariable Integer userId, @PathVariable Integer taskId) {
        Optional<UserTask> userTask = userTaskService.getUserTaskById(userId, taskId);
        return userTask.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<UserTask> getUserTasksByUserId(@PathVariable Integer userId) {
        return userTaskService.getUserTasksByUserId(userId);
    }

    @GetMapping("/task/{taskId}")
    public List<UserTask> getUserTasksByTaskId(@PathVariable Integer taskId) {
        return userTaskService.getUserTasksByTaskId(taskId);
    }

    @GetMapping("/task/{taskId}/users")
    public List<Integer> getUserIdsByTaskId(@PathVariable Integer taskId) {
        return userTaskService.getUserIdsByTaskId(taskId);
    }

    @GetMapping("/user/{userId}/tasks")
    public List<Integer> getTaskIdsByUserId(@PathVariable Integer userId) {
        return userTaskService.getTaskIdsByUserId(userId);
    }

    @GetMapping("/task/{taskId}/count")
    public ResponseEntity<Long> countUsersByTaskId(@PathVariable Integer taskId) {
        long count = userTaskService.countUsersByTaskId(taskId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countTasksByUserId(@PathVariable Integer userId) {
        long count = userTaskService.countTasksByUserId(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/exists/user/{userId}/task/{taskId}")
    public ResponseEntity<Boolean> existsUserTaskRelation(@PathVariable Integer userId, @PathVariable Integer taskId) {
        boolean exists = userTaskService.existsUserTaskRelation(userId, taskId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignUserToTask(@RequestBody Map<String, Object> body) {
        try {
            Integer userId = (Integer) body.get("userId");
            Integer taskId = (Integer) body.get("taskId");

            if (userId == null) {
                throw new IllegalArgumentException("ID de usuario es requerido");
            }

            if (taskId == null) {
                throw new IllegalArgumentException("ID de tarea es requerido");
            }

            UserTask assigned = userTaskService.assignUserToTask(userId, taskId);
            return ResponseEntity.ok(assigned);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/unassign/user/{userId}/task/{taskId}")
    public ResponseEntity<?> unassignUserFromTask(@PathVariable Integer userId, @PathVariable Integer taskId) {
        try {
            userTaskService.unassignUserFromTask(userId, taskId);
            return ResponseEntity.ok("Usuario desasignado de la tarea exitosamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteAllUserTasksByUserId(@PathVariable Integer userId) {
        try {
            userTaskService.deleteAllUserTasksByUserId(userId);
            return ResponseEntity.ok("Todas las asignaciones del usuario han sido eliminadas");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<?> deleteAllUserTasksByTaskId(@PathVariable Integer taskId) {
        try {
            userTaskService.deleteAllUserTasksByTaskId(taskId);
            return ResponseEntity.ok("Todas las asignaciones de la tarea han sido eliminadas");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
}
