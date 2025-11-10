package com.projectsapi.projectsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.projectsapi.projectsapi.model.UserRoleProject;
import com.projectsapi.projectsapi.model.UserRoleProjectId;
import java.util.List;


import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRoleProjectRepository extends JpaRepository<UserRoleProject, UserRoleProjectId> {
    List<UserRoleProject> findByIDProject(Integer IDProject);

       @Modifying
    @Transactional
    @Query(value = "INSERT INTO userroleproject (iduser, idrole, idproject) VALUES (:userId, :roleId, :projectId)", nativeQuery = true)
    void assignUserToProject(
        @Param("userId") Integer userId,
        @Param("roleId") Integer roleId,
        @Param("projectId") Integer projectId
    );


}
