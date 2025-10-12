package com.projectsapi.projectsapi.model;

import java.io.Serializable;
import java.util.Objects;

public class UserRoleProjectId implements Serializable {
    private Integer IDUser;
    private Integer IDRole;
    private Integer IDProject;

    public UserRoleProjectId() {}

    public UserRoleProjectId(Integer IDUser, Integer IDRole, Integer IDProject) {
        this.IDUser = IDUser;
        this.IDRole = IDRole;
        this.IDProject = IDProject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleProjectId that)) return false;
        return Objects.equals(IDUser, that.IDUser) &&
               Objects.equals(IDRole, that.IDRole) &&
               Objects.equals(IDProject, that.IDProject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDUser, IDRole, IDProject);
    }
}
