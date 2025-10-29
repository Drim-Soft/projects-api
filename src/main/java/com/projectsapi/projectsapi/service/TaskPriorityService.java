package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.TaskPriority;
import com.projectsapi.projectsapi.repository.TaskPriorityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskPriorityService {

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    // =====================================================
    // GET ALL TASK PRIORITIES
    // =====================================================
    public List<TaskPriority> getAllTaskPriorities() {
        return taskPriorityRepository.findAll();
    }

    // =====================================================
    // GET TASK PRIORITY BY ID
    // =====================================================
    public Optional<TaskPriority> getTaskPriorityById(Integer id) {
        return taskPriorityRepository.findById(id);
    }

    // =====================================================
    // GET TASK PRIORITY BY NAME
    // =====================================================
    public Optional<TaskPriority> getTaskPriorityByName(String name) {
        return taskPriorityRepository.findByNameIgnoreCase(name);
    }

    // =====================================================
    // CREATE TASK PRIORITY
    // =====================================================
    public TaskPriority createTaskPriority(TaskPriority taskPriority) {
        if (taskPriority.getName() == null || taskPriority.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task priority name is required");
        }

        // Verificar si ya existe una prioridad con el mismo nombre
        Optional<TaskPriority> existingPriority = taskPriorityRepository.findByNameIgnoreCase(taskPriority.getName());
        if (existingPriority.isPresent()) {
            throw new IllegalArgumentException("Task priority with name '" + taskPriority.getName() + "' already exists");
        }

        return taskPriorityRepository.save(taskPriority);
    }

    // =====================================================
    // UPDATE TASK PRIORITY
    // =====================================================
    public TaskPriority updateTaskPriority(Integer id, TaskPriority updatedTaskPriority) {
        TaskPriority existingTaskPriority = taskPriorityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task priority not found"));

        if (updatedTaskPriority.getName() == null || updatedTaskPriority.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task priority name is required");
        }

        // Verificar si ya existe otra prioridad con el mismo nombre (excluyendo la actual)
        Optional<TaskPriority> existingPriority = taskPriorityRepository.findByNameIgnoreCase(updatedTaskPriority.getName());
        if (existingPriority.isPresent() && !existingPriority.get().getIDTaskPriority().equals(id)) {
            throw new IllegalArgumentException("Task priority with name '" + updatedTaskPriority.getName() + "' already exists");
        }

        existingTaskPriority.setName(updatedTaskPriority.getName());
        return taskPriorityRepository.save(existingTaskPriority);
    }

    // =====================================================
    // DELETE TASK PRIORITY
    // =====================================================
    public void deleteTaskPriority(Integer id) {
        TaskPriority taskPriority = taskPriorityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task priority not found"));

        taskPriorityRepository.delete(taskPriority);
    }

    // =====================================================
    // CHECK IF TASK PRIORITY EXISTS
    // =====================================================
    public boolean existsById(Integer id) {
        return taskPriorityRepository.existsById(id);
    }

    // =====================================================
    // CHECK IF TASK PRIORITY EXISTS BY NAME
    // =====================================================
    public boolean existsByName(String name) {
        return taskPriorityRepository.findByNameIgnoreCase(name).isPresent();
    }
}
