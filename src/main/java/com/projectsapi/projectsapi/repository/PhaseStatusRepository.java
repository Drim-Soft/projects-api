package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.PhaseStatus;

import java.util.Optional;

@Repository
public interface PhaseStatusRepository extends JpaRepository<PhaseStatus, Integer> {
    Optional<PhaseStatus> findByNameIgnoreCase(String name);
}
