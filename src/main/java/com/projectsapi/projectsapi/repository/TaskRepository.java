package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    
    @Query("SELECT t FROM Task t JOIN FETCH t.phase WHERE t.IDTask = :id")
    Task findTaskWithPhase(@Param("id") Integer id);

    @Query("SELECT t FROM Task t JOIN FETCH t.taskStatus WHERE t.IDTask = :id")
    Task findTaskWithStatus(@Param("id") Integer id);

    @Query("SELECT t FROM Task t JOIN FETCH t.taskPriority WHERE t.IDTask = :id")
    Task findTaskWithPriority(@Param("id") Integer id);

    List<Task> findByIDUser(Integer IDUser);

    List<Task> findByPhase_IDPhase(Integer IDPhase);
}
