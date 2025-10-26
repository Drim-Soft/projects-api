package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.ProjectStatus;
import java.util.Optional;


@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Integer> {
    Optional<ProjectStatus> findByNameIgnoreCase(String name);
}
