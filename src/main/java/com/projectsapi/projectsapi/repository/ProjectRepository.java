package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectsapi.projectsapi.model.Project;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p JOIN FETCH p.methodology WHERE p.IDProject = :id")
Project findProjectWithMethodology(@Param("id") Integer id);

@Query("""
    SELECT DISTINCT p
    FROM Project p
    JOIN UserRoleProject urp ON urp.IDProject = p.IDProject
    WHERE urp.IDUser = :userId
""")
List<Project> findProjectsByUserId(@Param("userId") Integer userId);

}
