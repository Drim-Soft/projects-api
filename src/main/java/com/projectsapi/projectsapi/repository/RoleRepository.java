package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.projectsapi.projectsapi.model.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByMethodology_IDMethodology(Integer idMethodology);
    Optional<Role> findByNameIgnoreCase(String name);
}
