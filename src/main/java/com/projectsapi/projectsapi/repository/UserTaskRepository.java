package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.projectsapi.projectsapi.model.UserTask;
import com.projectsapi.projectsapi.model.UserTaskId;
import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, UserTaskId> {
    
    // Buscar todas las tareas asignadas a un usuario
    List<UserTask> findByIDUser(Integer IDUser);
    
    // Buscar todos los usuarios asignados a una tarea
    List<UserTask> findByIDTask(Integer IDTask);
    
    // Verificar si existe la relación usuario-tarea
    boolean existsByIDUserAndIDTask(Integer IDUser, Integer IDTask);
    
    // Eliminar relación específica usuario-tarea
    void deleteByIDUserAndIDTask(Integer IDUser, Integer IDTask);
    
    // Contar usuarios asignados a una tarea
    long countByIDTask(Integer IDTask);
    
    // Contar tareas asignadas a un usuario
    long countByIDUser(Integer IDUser);
    
    // Buscar usuarios asignados a una tarea específica
    @Query("SELECT ut.IDUser FROM UserTask ut WHERE ut.IDTask = :taskId")
    List<Integer> findUserIdsByTaskId(@Param("taskId") Integer taskId);
    
    // Buscar tareas asignadas a un usuario específico
    @Query("SELECT ut.IDTask FROM UserTask ut WHERE ut.IDUser = :userId")
    List<Integer> findTaskIdsByUserId(@Param("userId") Integer userId);
}
