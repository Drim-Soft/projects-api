package com.projectsapi.projectsapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projectsapi.projectsapi.model.TaskStatus;
import com.projectsapi.projectsapi.service.TaskStatusService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task-status")
@CrossOrigin(originPatterns = "*")
public class TaskStatusController {

    // =====================================================
    // TEST ENDPOINT
    // =====================================================
    @GetMapping("/test")
    public String test() {
        return "TaskStatus controller is working!";
    }

    @Autowired
    private TaskStatusService taskStatusService;

    // =====================================================
    // GET ALL TASK STATUSES
    // =====================================================
    @GetMapping
    public ResponseEntity<List<TaskStatus>> getAllTaskStatuses() {
        try {
            List<TaskStatus> taskStatuses = taskStatusService.getAllTaskStatuses();
            return ResponseEntity.ok(taskStatuses);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error in getAllTaskStatuses: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }

    // =====================================================
    // GET TASK STATUS BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatus> getTaskStatusById(@PathVariable Integer id) {
        try {
            Optional<TaskStatus> taskStatus = taskStatusService.getTaskStatusById(id);
            if (taskStatus.isPresent()) {
                return ResponseEntity.ok(taskStatus.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // GET TASK STATUS BY NAME
    // =====================================================
    @GetMapping("/name/{name}")
    public ResponseEntity<TaskStatus> getTaskStatusByName(@PathVariable String name) {
        try {
            Optional<TaskStatus> taskStatus = taskStatusService.getTaskStatusByName(name);
            if (taskStatus.isPresent()) {
                return ResponseEntity.ok(taskStatus.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // CREATE TASK STATUS
    // =====================================================
    @PostMapping
    public ResponseEntity<TaskStatus> createTaskStatus(@RequestBody TaskStatus taskStatus) {
        try {
            TaskStatus createdTaskStatus = taskStatusService.createTaskStatus(taskStatus);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTaskStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // UPDATE TASK STATUS
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatus> updateTaskStatus(@PathVariable Integer id, @RequestBody TaskStatus taskStatus) {
        try {
            TaskStatus updatedTaskStatus = taskStatusService.updateTaskStatus(id, taskStatus);
            return ResponseEntity.ok(updatedTaskStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // DELETE TASK STATUS
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Integer id) {
        try {
            taskStatusService.deleteTaskStatus(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // CHECK IF TASK STATUS EXISTS
    // =====================================================
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable Integer id) {
        try {
            boolean exists = taskStatusService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // CHECK IF TASK STATUS EXISTS BY NAME
    // =====================================================
    @GetMapping("/exists/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        try {
            boolean exists = taskStatusService.existsByName(name);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
