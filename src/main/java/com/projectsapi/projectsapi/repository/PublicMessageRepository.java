package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectsapi.projectsapi.model.PublicMessage;
import java.util.List;

@Repository
public interface PublicMessageRepository extends JpaRepository<PublicMessage, Integer> {
    
    // Buscar mensajes por ID de tarea
    List<PublicMessage> findByTaskIDTaskOrderByDateDesc(Integer IDTask);
    
    // Buscar mensajes por ID de usuario
    List<PublicMessage> findByIDUserOrderByDateDesc(Integer IDUser);
    
    // Buscar mensajes por ID de tarea y usuario
    List<PublicMessage> findByTaskIDTaskAndIDUserOrderByDateDesc(Integer IDTask, Integer IDUser);
    
    // Contar mensajes por tarea
    long countByTaskIDTask(Integer IDTask);
}
