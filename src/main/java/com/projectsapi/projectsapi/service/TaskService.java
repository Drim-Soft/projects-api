package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projectsapi.projectsapi.model.Phase;
import com.projectsapi.projectsapi.model.Task;
import com.projectsapi.projectsapi.model.TaskStatus;
import com.projectsapi.projectsapi.model.TaskPriority;
import com.projectsapi.projectsapi.repository.TaskRepository;
import com.projectsapi.projectsapi.repository.PhaseRepository;
import com.projectsapi.projectsapi.repository.TaskStatusRepository;
import com.projectsapi.projectsapi.repository.TaskPriorityRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PhaseRepository phaseRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;

    // =====================================================
    // GET ALL TASKS
    // =====================================================
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // =====================================================
    // GET TASK BY ID
    // =====================================================
    public Optional<Task> getTaskById(Integer id) {
        return taskRepository.findById(id);
    }

    // =====================================================
    // GET TASKS BY USER
    // =====================================================
    public List<Task> getTasksByUser(Integer IDUser) {
        return taskRepository.findByIDUser(IDUser);
    }

    // =====================================================
    // GET TASKS BY PHASE
    // =====================================================
    public List<Task> getTasksByPhase(Integer IDPhase) {
        return taskRepository.findByPhase_IDPhase(IDPhase);
    }

    // =====================================================
    // CREATE TASK
    // =====================================================
    public Task createTask(Task task) {
        System.out.println("📩 Recibido: PhaseRef=" + task.getIDPhaseRef() +
                        " | TaskStatusRef=" + task.getIDTaskStatusRef() +
                        " | TaskPriorityRef=" + task.getIDTaskPriorityRef());

        if (task.getIDPhaseRef() == null) {
            throw new IllegalArgumentException("Phase ID is required");
        }
        if (task.getIDTaskStatusRef() == null) {
            throw new IllegalArgumentException("TaskStatus ID is required");
        }
        if (task.getIDTaskPriorityRef() == null) {
            throw new IllegalArgumentException("TaskPriority ID is required");
        }

        Phase phase = phaseRepository.findById(task.getIDPhaseRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Phase ID"));
        TaskStatus status = taskStatusRepository.findById(task.getIDTaskStatusRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid TaskStatus ID"));
        TaskPriority priority = taskPriorityRepository.findById(task.getIDTaskPriorityRef())
                .orElseThrow(() -> new IllegalArgumentException("Invalid TaskPriority ID"));

        task.setPhase(phase);
        task.setTaskStatus(status);
        task.setTaskPriority(priority);

        return taskRepository.save(task);
    }

    // =====================================================
    // UPDATE TASK
    // =====================================================
    public Task updateTask(Integer id, Task updatedTask) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        existingTask.setName(updatedTask.getName());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setStartDate(updatedTask.getStartDate());
        existingTask.setEndDate(updatedTask.getEndDate());
        existingTask.setTimeInvested(updatedTask.getTimeInvested());
        existingTask.setPercentageProgress(updatedTask.getPercentageProgress());
        existingTask.setBudget(updatedTask.getBudget());
        existingTask.setCost(updatedTask.getCost());
        existingTask.setFileURL(updatedTask.getFileURL());
        existingTask.setScore(updatedTask.getScore());
        existingTask.setFeedback(updatedTask.getFeedback());
        existingTask.setIDUser(updatedTask.getIDUser());

        if (updatedTask.getIDPhaseRef() != null) {
            Phase phase = phaseRepository.findById(updatedTask.getIDPhaseRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Phase ID"));
            existingTask.setPhase(phase);
        }

        if (updatedTask.getIDTaskStatusRef() != null) {
            TaskStatus status = taskStatusRepository.findById(updatedTask.getIDTaskStatusRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid TaskStatus ID"));
            existingTask.setTaskStatus(status);
        }

        if (updatedTask.getIDTaskPriorityRef() != null) {
            TaskPriority priority = taskPriorityRepository.findById(updatedTask.getIDTaskPriorityRef())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid TaskPriority ID"));
            existingTask.setTaskPriority(priority);
        }

        return taskRepository.save(existingTask);
    }

    // =====================================================
    // DELETE TASK (BORRADO LÓGICO)
    // =====================================================
    public void deleteTask(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        TaskStatus deletedStatus = taskStatusRepository.findAll().stream()
                .filter(ts -> ts.getName().equalsIgnoreCase("Deleted") ||
                              ts.getName().equalsIgnoreCase("Eliminado"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Deleted status not found"));

        task.setTaskStatus(deletedStatus);
        taskRepository.save(task);
    }
}