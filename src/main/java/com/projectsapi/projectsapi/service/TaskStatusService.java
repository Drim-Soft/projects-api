package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.TaskStatus;
import com.projectsapi.projectsapi.repository.TaskStatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    // =====================================================
    // GET ALL TASK STATUSES
    // =====================================================
    public List<TaskStatus> getAllTaskStatuses() {
        return taskStatusRepository.findAll();
    }

    // =====================================================
    // GET TASK STATUS BY ID
    // =====================================================
    public Optional<TaskStatus> getTaskStatusById(Integer id) {
        return taskStatusRepository.findById(id);
    }

    // =====================================================
    // GET TASK STATUS BY NAME
    // =====================================================
    public Optional<TaskStatus> getTaskStatusByName(String name) {
        return taskStatusRepository.findByNameIgnoreCase(name);
    }

    // =====================================================
    // CREATE TASK STATUS
    // =====================================================
    public TaskStatus createTaskStatus(TaskStatus taskStatus) {
        if (taskStatus.getName() == null || taskStatus.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task status name is required");
        }

        // Verificar si ya existe un estado con el mismo nombre
        Optional<TaskStatus> existingStatus = taskStatusRepository.findByNameIgnoreCase(taskStatus.getName());
        if (existingStatus.isPresent()) {
            throw new IllegalArgumentException("Task status with name '" + taskStatus.getName() + "' already exists");
        }

        return taskStatusRepository.save(taskStatus);
    }

    // =====================================================
    // UPDATE TASK STATUS
    // =====================================================
    public TaskStatus updateTaskStatus(Integer id, TaskStatus updatedTaskStatus) {
        TaskStatus existingTaskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task status not found"));

        if (updatedTaskStatus.getName() == null || updatedTaskStatus.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task status name is required");
        }

        // Verificar si ya existe otro estado con el mismo nombre (excluyendo el actual)
        Optional<TaskStatus> existingStatus = taskStatusRepository.findByNameIgnoreCase(updatedTaskStatus.getName());
        if (existingStatus.isPresent() && !existingStatus.get().getIDTaskStatus().equals(id)) {
            throw new IllegalArgumentException("Task status with name '" + updatedTaskStatus.getName() + "' already exists");
        }

        existingTaskStatus.setName(updatedTaskStatus.getName());
        return taskStatusRepository.save(existingTaskStatus);
    }

    // =====================================================
    // DELETE TASK STATUS
    // =====================================================
    public void deleteTaskStatus(Integer id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task status not found"));

        taskStatusRepository.delete(taskStatus);
    }

    // =====================================================
    // CHECK IF TASK STATUS EXISTS
    // =====================================================
    public boolean existsById(Integer id) {
        return taskStatusRepository.existsById(id);
    }

    // =====================================================
    // CHECK IF TASK STATUS EXISTS BY NAME
    // =====================================================
    public boolean existsByName(String name) {
        return taskStatusRepository.findByNameIgnoreCase(name).isPresent();
    }
}
