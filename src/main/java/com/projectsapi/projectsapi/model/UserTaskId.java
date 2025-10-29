package com.projectsapi.projectsapi.model;

import java.io.Serializable;
import java.util.Objects;

public class UserTaskId implements Serializable {

    private Integer IDUser;
    private Integer IDTask;

    // Constructor por defecto
    public UserTaskId() {}

    // Constructor con parámetros
    public UserTaskId(Integer IDUser, Integer IDTask) {
        this.IDUser = IDUser;
        this.IDTask = IDTask;
    }

    // Getters y setters
    public Integer getIDUser() { 
        return IDUser; 
    }
    
    public void setIDUser(Integer IDUser) { 
        this.IDUser = IDUser; 
    }

    public Integer getIDTask() { 
        return IDTask; 
    }
    
    public void setIDTask(Integer IDTask) { 
        this.IDTask = IDTask; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTaskId that = (UserTaskId) o;
        return Objects.equals(IDUser, that.IDUser) && Objects.equals(IDTask, that.IDTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IDUser, IDTask);
    }
}
