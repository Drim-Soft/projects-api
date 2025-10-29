package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.Methodology;
import java.util.Optional;


@Repository
public interface MethodologyRepository extends JpaRepository<Methodology, Integer> {
    Optional<Methodology> findByNameIgnoreCase(String name);
}
