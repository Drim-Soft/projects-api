package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projectsapi.projectsapi.model.UserTask;
import com.projectsapi.projectsapi.model.UserTaskId;
import com.projectsapi.projectsapi.repository.UserTaskRepository;
import com.projectsapi.projectsapi.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserTaskService {

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private TaskRepository taskRepository;

    public List<UserTask> getAllUserTasks() {
        return userTaskRepository.findAll();
    }

    public Optional<UserTask> getUserTaskById(Integer userId, Integer taskId) {
        UserTaskId id = new UserTaskId(userId, taskId);
        return userTaskRepository.findById(id);
    }

    public List<UserTask> getUserTasksByUserId(Integer userId) {
        return userTaskRepository.findByIDUser(userId);
    }

    public List<UserTask> getUserTasksByTaskId(Integer taskId) {
        return userTaskRepository.findByIDTask(taskId);
    }

    public List<Integer> getUserIdsByTaskId(Integer taskId) {
        return userTaskRepository.findUserIdsByTaskId(taskId);
    }

    public List<Integer> getTaskIdsByUserId(Integer userId) {
        return userTaskRepository.findTaskIdsByUserId(userId);
    }

    public long countUsersByTaskId(Integer taskId) {
        return userTaskRepository.countByIDTask(taskId);
    }

    public long countTasksByUserId(Integer userId) {
        return userTaskRepository.countByIDUser(userId);
    }

    public boolean existsUserTaskRelation(Integer userId, Integer taskId) {
        return userTaskRepository.existsByIDUserAndIDTask(userId, taskId);
    }

    public UserTask assignUserToTask(Integer userId, Integer taskId) {
        // Validar que la tarea existe
        if (!taskRepository.existsById(taskId)) {
            throw new IllegalArgumentException("No se encontró la tarea con ID: " + taskId);
        }

        // Verificar si la relación ya existe
        if (userTaskRepository.existsByIDUserAndIDTask(userId, taskId)) {
            throw new IllegalArgumentException("El usuario ya está asignado a esta tarea");
        }

        // Validar que el usuario esté especificado
        if (userId == null) {
            throw new IllegalArgumentException("ID de usuario es requerido");
        }

        UserTask userTask = new UserTask(userId, taskId);
        return userTaskRepository.save(userTask);
    }

    public void unassignUserFromTask(Integer userId, Integer taskId) {
        // Verificar si la relación existe
        if (!userTaskRepository.existsByIDUserAndIDTask(userId, taskId)) {
            throw new IllegalArgumentException("No se encontró la asignación del usuario a esta tarea");
        }

        userTaskRepository.deleteByIDUserAndIDTask(userId, taskId);
    }

    public void deleteUserTask(UserTaskId id) {
        if (userTaskRepository.existsById(id)) {
            userTaskRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("No se encontró la asignación usuario-tarea");
        }
    }

    public void deleteAllUserTasksByUserId(Integer userId) {
        List<UserTask> userTasks = userTaskRepository.findByIDUser(userId);
        userTaskRepository.deleteAll(userTasks);
    }

    public void deleteAllUserTasksByTaskId(Integer taskId) {
        List<UserTask> userTasks = userTaskRepository.findByIDTask(taskId);
        userTaskRepository.deleteAll(userTasks);
    }
}
