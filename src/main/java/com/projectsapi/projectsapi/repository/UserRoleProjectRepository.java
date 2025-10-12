package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projectsapi.projectsapi.model.UserRoleProject;
import com.projectsapi.projectsapi.model.UserRoleProjectId;
import java.util.List;

public interface UserRoleProjectRepository extends JpaRepository<UserRoleProject, UserRoleProjectId> {
    List<UserRoleProject> findByIDProject(Integer IDProject);
}
