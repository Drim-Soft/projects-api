package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.TaskPriority;
import com.projectsapi.projectsapi.service.TaskPriorityService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task-priority")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://traducianistic-immorally-harmony.ngrok-free.dev",
        "https://nonblunderingly-micropaleontological-mabelle.ngrok-free.dev",
        "https://preceremonial-forgivingly-natisha.ngrok-free.dev"
})
public class TaskPriorityController {

    @Autowired
    private TaskPriorityService taskPriorityService;

    // =====================================================
    // GET ALL TASK PRIORITIES
    // =====================================================
    @GetMapping
    public ResponseEntity<List<TaskPriority>> getAllTaskPriorities() {
        List<TaskPriority> priorities = taskPriorityService.getAllTaskPriorities();
        return ResponseEntity.ok(priorities);
    }

    // =====================================================
    // GET TASK PRIORITY BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<TaskPriority> getTaskPriorityById(@PathVariable Integer id) {
        Optional<TaskPriority> taskPriority = taskPriorityService.getTaskPriorityById(id);
        return taskPriority.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =====================================================
    // GET TASK PRIORITY BY NAME
    // =====================================================
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskPriority> getTaskPriorityByName(@PathVariable String name) {
        Optional<TaskPriority> taskPriority = taskPriorityService.getTaskPriorityByName(name);
        return taskPriority.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // =====================================================
    // CREATE TASK PRIORITY
    // =====================================================
    @PostMapping
    public ResponseEntity<?> createTaskPriority(@RequestBody TaskPriority taskPriority) {
        try {
            TaskPriority created = taskPriorityService.createTaskPriority(taskPriority);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // =====================================================
    // UPDATE TASK PRIORITY
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaskPriority(@PathVariable Integer id, @RequestBody TaskPriority taskPriority) {
        try {
            TaskPriority updated = taskPriorityService.updateTaskPriority(id, taskPriority);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // =====================================================
    // DELETE TASK PRIORITY
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskPriority(@PathVariable Integer id) {
        try {
            taskPriorityService.deleteTaskPriority(id);
            return ResponseEntity.ok("Task priority deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    // =====================================================
    // CHECK IF TASK PRIORITY EXISTS
    // =====================================================
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Integer id) {
        boolean exists = taskPriorityService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    // =====================================================
    // CHECK IF TASK PRIORITY EXISTS BY NAME
    // =====================================================
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        boolean exists = taskPriorityService.existsByName(name);
        return ResponseEntity.ok(exists);
    }
}
