package com.projectsapi.projectsapi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "UserRoleProject")
@IdClass(UserRoleProjectId.class)
public class UserRoleProject {

    @Id
    @Column(name = "IDUser")
    private Integer IDUser;

    @Id
    @Column(name = "IDRole")
    private Integer IDRole;

    @Id
    @Column(name = "IDProject")
    private Integer IDProject;

    public Integer getIDUser() {
        return IDUser;
    }

    public void setIDUser(Integer IDUser) {
        this.IDUser = IDUser;
    }

    public Integer getIDRole() {
        return IDRole;
    }

    public void setIDRole(Integer IDRole) {
        this.IDRole = IDRole;
    }

    public Integer getIDProject() {
        return IDProject;
    }

    public void setIDProject(Integer IDProject) {
        this.IDProject = IDProject;
    }
}
