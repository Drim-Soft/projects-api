package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.Phase;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Integer> {
    
    @Query("SELECT p FROM Phase p JOIN FETCH p.project WHERE p.IDPhase = :id")
    Phase findPhaseWithProject(@Param("id") Integer id);

    List<Phase> findByProject_IDProject(Integer IDProject);

    @Query("SELECT p FROM Phase p WHERE p.phaseStatus.id <> 3")
    List<Phase> findAllActive();


}
